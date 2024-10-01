package com.example.playlistmaker.presentation.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.adapters.TrackAdapter
import com.example.playlistmaker.ui.player.PlayerActivity
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorLayout: LinearLayout
    private lateinit var retryButton: TextView
    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: TextView
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()


    private var tracks: MutableList<Track>? = mutableListOf()
    private var searchText: String = ""


    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch(searchText) }



    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private var isClickAllowed = true

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<View>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        clearButton = findViewById(R.id.clearButton)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        errorLayout = findViewById(R.id.errorLayout)
        retryButton = findViewById(R.id.retryButton)
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val historyState = hasFocus && searchEditText.text.isEmpty()
            showHistory(historyState)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val historyState = searchEditText.hasFocus() && searchEditText.text.isEmpty()
                showHistory(historyState)
                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(clearButton.windowToken, 0)
            searchEditText.clearFocus()
            clearAdapter()
            clearError()
            hideHistory(false)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Заглушка для будущих задач
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показываем или скрываем кнопку сброса в зависимости от наличия текста
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // Заглушка для будущих задач
            }
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        // Скрываем кнопку сброса, если строка поиска пустая
        clearButton.isVisible = searchEditText.text.isNotEmpty()

        // Восстанавливаем состояние
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            searchEditText.setText(searchText)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        retryButton.setOnClickListener {
            // Скрываем сообщение об ошибке перед новым поиском
            errorLayout.isVisible = false
            recyclerView?.isVisible = true

            // Проверяем, что поле поиска не пустое, перед запуском поиска
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query) // Выполняем повторный поиск
            }
        }

        setupRecyclerView()
        updateSearchHistory()
        showHistory(false)

        clearHistoryButton.setOnClickListener {
            // Очищаем историю через интерактор
            clearHistory()

            // Обновляем интерфейс
            updateSearchHistory() // Перерисовываем список истории
            showHistory(false) // Прячем историю после её очистки
            searchEditText.clearFocus() // Убираем фокус с поля ввода
        }

    }

    private fun setupRecyclerView() {
        searchHistoryAdapter = TrackAdapter(emptyList()) { track ->
            clickDebounce()
            onTrackClick(track)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchHistoryAdapter
    }

    private fun onTrackClick(track: Track) {
        saveTrackToHistory(track)// Сохраняем трек в истории через интерактор
        updateSearchHistory()

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(App.TRACK_DT, Gson().toJson(track))
        startActivity(intent)
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            // Скрываем сообщение об ошибке перед новым поиском
            errorLayout.isVisible = false // Скрываем сообщение об ошибке "Ничего не найдено"
            recyclerView.isVisible = false // Скрываем RecyclerView
            progressBar.isVisible = true// Показываем прогресс-бар

            // Вызов интерактора для поиска треков
            tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    // Убеждаемся, что UI обновляется на главном (основном) потоке
                    runOnUiThread {
                        progressBar.isVisible = false // Прячем прогресс-бар
                        if (foundTracks.isNotEmpty()) {
                            recyclerView.isVisible = true
                            updateTracks(foundTracks) // Обновляем RecyclerView с результатами
                        } else {
                            showNotFoundError() // Показываем ошибку "не найдено"
                        }
                    }
                }
            }) { throwable ->
                // Обработка ошибки (например, отсутствие интернета)
                runOnUiThread {
                    progressBar.isVisible =false
                    if (throwable is IOException) {
                        showNetworkError() // Показываем ошибку сети
                    } else {
                        showNotFoundError() // Если это другая ошибка
                    }
                }
            }
        }
    }

    private fun updateTracks(foundTracks: List<Track>) {
        tracks = foundTracks.toMutableList()
        recyclerView.adapter = TrackAdapter(tracks!!) { track ->
            onTrackClick(track)
        }
    }


    private fun updateSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        searchHistoryAdapter.updateTracks(history)
    }
    private fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrack(track)
        updateSearchHistory()
    }
    private fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        updateSearchHistory()
    }

    private fun showNotFoundError(){
        errorLayout.isVisible = true
        recyclerView.isVisible = false
        clearAdapter()
        retryButton.isVisible = false
        errorText.setText(R.string.text_error)
        errorImage.setImageResource(R.drawable.error_empty)
    }

    private fun showNetworkError(){
        errorLayout.isVisible = true
        recyclerView.isVisible = false
        clearAdapter()
        retryButton.isVisible = true
        errorText.setText(R.string.internet_error)
        errorImage.setImageResource(R.drawable.error_internet)
    }

    private fun showHistory(state: Boolean) {
        if (state) {
            hideHistory(true)
            setupRecyclerView()
            updateSearchHistory()
        } else {
            hideHistory(false)
            tracks?.clear()
            recyclerView.adapter = TrackAdapter(tracks!!){ track ->
                onTrackClick(track)
            }
        }
    }



    private fun clearError() {
        errorLayout.isVisible = false
        recyclerView.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAdapter() {
        tracks?.clear()
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun hideHistory(state: Boolean) {
        var stateCount = state
        if (searchHistoryAdapter.itemCount <= 0) {
            stateCount = false
        }

        searchHistoryTitle.isVisible = stateCount
        clearHistoryButton.isVisible = stateCount

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        searchEditText.setText(searchText)
        performSearch(searchText)
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

}
