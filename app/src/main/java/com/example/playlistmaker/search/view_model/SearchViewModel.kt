package com.example.playlistmaker.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.search.model.SearchScreenState
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val screenState = MutableLiveData<SearchScreenState>()
    fun getScreenState(): LiveData<SearchScreenState> = screenState

    private var lastSearchQuery: String? = null
    private var lastSearchResults: List<Track>? = null
    private var isShowingHistory = true
    private var isLoading = false

    init {
        updateSearchHistory()
    }

    fun performSearch(query: String) {
        if (isLoading) return // Если идет загрузка, игнорируем новые запросы

        // Если запрос пустой, показываем историю
        if (query.isBlank()) {
            updateSearchHistory()
            return
        }

        // Проверка, если запрос отличается от последнего
        if (query != lastSearchQuery) {
            lastSearchQuery = query
            isShowingHistory = false
            isLoading = true  // Устанавливаем флаг загрузки


            screenState.value = SearchScreenState.Loading

            viewModelScope.launch {
                try {
                    tracksInteractor.searchTracks(query).collect { tracks ->
                        if (tracks.isNotEmpty()) {
                            lastSearchResults = tracks
                            screenState.postValue(SearchScreenState.ShowSearchResults(tracks))
                        } else {
                            screenState.postValue(SearchScreenState.Error(R.string.text_error))
                        }
                    }
                } catch (e: IOException) {
                    screenState.postValue(SearchScreenState.Error(R.string.internet_error))
                } finally {
                    isLoading = false  // Останавливаем загрузку после завершения поиска
                }
            }
        }
    }

    fun updateSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.getHistory().collect { history ->
                isShowingHistory = true
                if (history.isNotEmpty()) {
                    screenState.postValue(SearchScreenState.ShowHistory(history))
                } else {
                    screenState.postValue(SearchScreenState.Empty)
                }
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.saveTrack(track)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            updateSearchHistory()
        }
    }

    fun restoreLastState() {
        if (isShowingHistory) {
            updateSearchHistory()
        } else {
            // Загружаем данные, только если они ещё не были загружены
            lastSearchQuery?.let {
                if (lastSearchResults == null) {  // Если результатов нет, выполняем поиск
                    performSearch(it)
                } else {
                    screenState.postValue(SearchScreenState.ShowSearchResults(lastSearchResults!!))
                }
            }
        }
    }
}
