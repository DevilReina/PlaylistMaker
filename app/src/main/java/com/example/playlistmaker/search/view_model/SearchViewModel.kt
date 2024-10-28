package com.example.playlistmaker.search.view_model

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

    private var lastSearchQuery: String? = null
    private var lastSearchResults: List<Track>? = null
    private var isShowingHistory = true

    init {
        updateSearchHistory()
    }

    fun performSearch(query: String) {
        lastSearchQuery = query
        isShowingHistory = false
        screenState.value = SearchScreenState.Loading

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                if (foundTracks.isNotEmpty()) {
                    lastSearchResults = foundTracks
                    screenState.postValue(SearchScreenState.ShowSearchResults(foundTracks))
                } else {
                    screenState.postValue(SearchScreenState.Error(R.string.text_error))
                }
            }
        }) { throwable ->
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
        isShowingHistory = true
        if (history.isNotEmpty()) {
            screenState.postValue(SearchScreenState.ShowHistory(history))
        } else {
            screenState.postValue(SearchScreenState.Empty)
        }
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        updateSearchHistory()
    }

    fun restoreLastState() {
        if (isShowingHistory) {
            updateSearchHistory()
        } else {
            lastSearchQuery?.let { performSearch(it) }
        }
    }

    fun setLastSearchState() {
        lastSearchResults?.let { screenState.postValue(SearchScreenState.ShowSearchResults(it)) }
    }
}


