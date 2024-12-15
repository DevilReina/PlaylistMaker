package com.example.playlistmaker.media.model

import com.example.playlistmaker.search.model.Track

sealed interface FavoriteTracksState {

    object Loading: FavoriteTracksState

    data class Content(
        val tracks: List<Track>
    ): FavoriteTracksState

    data class Empty(
        val message: String
    ): FavoriteTracksState
}