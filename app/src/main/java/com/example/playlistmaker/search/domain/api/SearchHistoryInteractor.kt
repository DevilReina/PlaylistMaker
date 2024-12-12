package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    suspend fun saveTrack(track: Track)
    fun getHistory(): Flow<List<Track>>
    suspend fun clearHistory()
}