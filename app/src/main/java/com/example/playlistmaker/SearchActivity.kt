package com.example.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var hintMessage: TextView
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorLayout: LinearLayout
    private lateinit var retryButton: TextView
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: TextView
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val emptyList: MutableList<Track> = mutableListOf()

    private var tracks: MutableList<Track>? = mutableListOf()
    private var searchText: String = ""

    private val BASE_URL = "https://itunes.apple.com"
    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences("SEARCH_PREFS", MODE_PRIVATE))

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
        hintMessage = findViewById(R.id.searchHint)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            hintMessage.visibility = if (hasFocus && searchEditText.text.isEmpty()) View.VISIBLE else View.GONE
            val historyState = hasFocus && searchEditText.text.isEmpty()
            showHistory(historyState)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hintMessage.visibility = if (searchEditText.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE
                val historyState = searchEditText.hasFocus() && searchEditText.text.isEmpty()
                showHistory(historyState)
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
            performSearch(searchEditText.text.toString())
        }

        // Инициализация Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)

        setupRecyclerView()
        updateSearchHistory()
        showHistory(false)

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateSearchHistory()
            showHistory(false)
            searchEditText.clearFocus()
        }
    }

    private fun setupRecyclerView() {
        searchHistoryAdapter = TrackAdapter(emptyList()) { track ->
            onTrackClick(track)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchHistoryAdapter
    }

    private fun onTrackClick(track: Track) {
        searchHistory.saveTrack(track)
        updateSearchHistory()

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(App.TRACK_DT, Gson().toJson(track))
        startActivity(intent)
    }

    private fun performSearch(query: String) {
        api.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful && response.body()?.resultCount ?: 0 > 0) {
                    tracks = response.body()?.results
                    recyclerView?.adapter = TrackAdapter(tracks!!){ track ->
                        onTrackClick(track)
                    }
                    clearError()
                } else {
                    showNotFoundError()

                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showNetworkError()
            }
        })
    }


    private fun updateSearchHistory() {
        val history = searchHistory.getHistory()
        searchHistoryAdapter.updateTracks(history)
    }


    private fun showNotFoundError(){
        errorLayout.isVisible = true
        recyclerView?.isVisible = false
        clearAdapter()
        retryButton.isVisible = false
        errorText.setText(R.string.text_error)
        errorImage.setImageResource(R.drawable.error_empty)
    }

    private fun showNetworkError(){
        errorLayout.isVisible = true
        recyclerView?.isVisible = false
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
            recyclerView?.adapter = TrackAdapter(tracks!!){ track ->
                onTrackClick(track)
            }
        }
    }

    private fun clearError() {
        errorLayout.isVisible = false
        recyclerView?.isVisible = true
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
    }

}
