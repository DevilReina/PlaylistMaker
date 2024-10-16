package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.model.Track

import com.example.playlistmaker.search.model.SearchScreenState
import java.io.IOException

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val screenState = MutableLiveData<SearchScreenState>()
    fun getScreenState(): LiveData<SearchScreenState> = screenState

    fun performSearch(query: String) {
        if (query.isEmpty()) return

        screenState.value = SearchScreenState.Loading

        // Вызов интерактора для поиска треков
        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                // Если треки найдены, обновляем состояние
                if (foundTracks.isNotEmpty()) {
                    screenState.postValue(SearchScreenState.ShowSearchResults(foundTracks))
                } else {
                    screenState.postValue(SearchScreenState.Error(R.string.text_error))
                }
            }
        }) { throwable ->
            // В случае ошибки, обновляем состояние на Error
            val errorMessageResId = if (throwable is IOException) {
                R.string.internet_error
            } else {
                R.string.text_error
            }
            screenState.postValue(SearchScreenState.Error(errorMessageResId))
        }
    }
    fun updateSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            screenState.postValue(SearchScreenState.ShowHistory(history))
        } else {
            screenState.postValue(SearchScreenState.Empty)
        }
    }
    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrack(track)
        updateSearchHistory()
    }
    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        updateSearchHistory()
    }

}

