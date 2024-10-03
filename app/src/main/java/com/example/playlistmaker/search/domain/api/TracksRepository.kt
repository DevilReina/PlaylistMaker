package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}