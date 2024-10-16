package com.example.playlistmaker.search.model

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class ShowSearchResults(val tracks: List<Track>) : SearchScreenState()
    data class ShowHistory(val historyTracks: List<Track>) : SearchScreenState()
    data class Error(val messageResId: Int) : SearchScreenState()
    object Empty : SearchScreenState()
}