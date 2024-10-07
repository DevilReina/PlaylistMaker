package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.model.Track

interface TracksInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer, onError: (Throwable) -> Unit)

     interface TracksConsumer {
         fun consume(foundTracks: List<Track>)
     }
}