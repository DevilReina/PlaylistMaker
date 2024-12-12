package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(query: String): Flow<List<Track>>
}