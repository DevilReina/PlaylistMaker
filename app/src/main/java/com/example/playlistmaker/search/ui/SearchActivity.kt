package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.search.adapters.TrackAdapter
import com.example.playlistmaker.search.model.HistoryState
import com.example.playlistmaker.search.model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.player.ui.PlayerActivity


class SearchActivity : AppCompatActivity() {


    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.provideFactory(
            Creator.provideTracksInteractor()
        )
    }
    private lateinit var searchHistoryAdapter: TrackAdapter
    private var tracks: MutableList<Track> = mutableListOf()

    private var searchText: String = ""

    private val handler = Handler(Looper.getMainLooper())


    private val searchRunnable = Runnable {
        if (binding.searchEditText.text.toString().isNotEmpty())
            viewModel.performSearch(binding.searchEditText.text.toString())
    }

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

        // Инициализируем ViewBinding
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Настраиваем RecyclerView для результатов поиска
        setupRecyclerView()
        observeSearchState()
        observeHistoryState()
        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val historyState = hasFocus && binding.searchEditText.text.isEmpty()
            showHistory(historyState)
        }



        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            hideKeyboard()
            binding.searchEditText.clearFocus()
            clearAdapter()
            clearError()
            hideHistory(false)
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            val historyState = binding.searchEditText.hasFocus() && text.isNullOrEmpty()
            clearError()
            showHistory(historyState)
            searchDebounce()
            // Показываем или скрываем кнопку сброса в зависимости от наличия текста
            binding.clearButton.isVisible = !text.isNullOrEmpty()
            searchText = text.toString()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.performSearch(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        // Скрываем кнопку сброса, если строка поиска пустая
        binding.clearButton.isVisible = binding.searchEditText.text.isNotEmpty()

        // Восстанавливаем состояние
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            binding.searchEditText.setText(searchText)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.retryButton.setOnClickListener {
            // Скрываем сообщение об ошибке перед новым поиском
            binding.errorLayout.isVisible = false
            binding.recyclerView.isVisible = true

            // Проверяем, что поле поиска не пустое, перед запуском поиска
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.performSearch(query) // Выполняем повторный поиск
            }
        }

        setupRecyclerView()
        updateSearchHistory()
        showHistory(false)

        binding.clearHistoryButton.setOnClickListener {
            // Очищаем историю через интерактор
            clearHistory()

            // Обновляем интерфейс
            updateSearchHistory() // Обновляем список истории
            showHistory(false) // Прячем историю
            binding.searchEditText.clearFocus() // Убираем фокус с поля ввода
        }

    }


    private fun setupRecyclerView() {
        searchHistoryAdapter = TrackAdapter(emptyList()) { track ->
            clickDebounce()
            onTrackClick(track)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = searchHistoryAdapter
    }


    private fun observeSearchState() {
        viewModel.getSearchState().observe(this) { searchState ->
            hideErrors()
            when (searchState) {
                is SearchState.Loading -> {
                    binding.recyclerView.isVisible = false
                    binding.progressBar.isVisible = true
                    binding.searchHistoryTitle.isVisible = false
                }
                is SearchState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerView.isVisible = true

                    // Обновляем список треков
                    updateTracks(searchState.tracks)
                }
                is SearchState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerView.isVisible = false
                    binding.errorLayout.isVisible = true
                    binding.errorText.text = getString(searchState.messageResId) // Показать сообщение об ошибке
                }
                is SearchState.History -> {
                    showHistory(true)
                }
            }
        }
    }

    private fun observeHistoryState() {
        viewModel.getHistoryState().observe(this) { historyState ->
            when (historyState) {
                is HistoryState.ShowHistory -> {
                    searchHistoryAdapter.updateTracks(historyState.historyTracks)
                    binding.searchHistoryTitle.isVisible = true
                }
                is HistoryState.HideHistory -> {
                    searchHistoryAdapter.updateTracks(emptyList())
                    binding.searchHistoryTitle.isVisible = false
                }
            }
        }
    }


    private fun onTrackClick(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(App.TRACK_DT, Gson().toJson(track))
        startActivity(intent)
        // Обновляем историю с задержкой, чтобы сначала было мгновенное действие
        Handler(Looper.getMainLooper()).postDelayed({
            saveTrackToHistory(track) // Сохраняем трек в истории через интерактор
            updateSearchHistory()
        }, 200)  // 200 мс задержка для асинхронности

    }

    private fun updateTracks(foundTracks: List<Track>) {
        tracks = foundTracks.toMutableList()
    binding.recyclerView.adapter = TrackAdapter(tracks) { track ->
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

    private fun showHistory(state: Boolean) {
        if (state) {
            hideHistory(true)
            setupRecyclerView()
            updateSearchHistory()
        } else {
            hideHistory(false)
            tracks.clear()
            binding.recyclerView.adapter = TrackAdapter(tracks){ track ->
                onTrackClick(track)
            }
        }
    }

    private fun clearError() {
        binding.errorLayout.isVisible = false
        binding.recyclerView.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAdapter() {
        tracks.clear()
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun hideHistory(state: Boolean) {
        var stateCount = state
        if (searchHistoryAdapter.itemCount <= 0) {
            stateCount = false
        }

        binding.searchHistoryTitle.isVisible = stateCount
        binding.clearHistoryButton.isVisible = stateCount

    }

    private fun hideErrors() {
        binding.errorLayout.isVisible = false
        binding.recyclerView.isVisible = false
    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.clearButton.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        binding.searchEditText.setText(searchText)
        viewModel.performSearch(searchText)
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

}
