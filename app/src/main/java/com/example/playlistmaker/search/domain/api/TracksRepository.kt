package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<List<Track>>
}