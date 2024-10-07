package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.model.Track

interface SearchHistoryInteractor {
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}