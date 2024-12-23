package com.example.playlistmaker.media.model

sealed class AddTrackToPlaylistState {

    data object Default: AddTrackToPlaylistState()

    data class TrackAdded(val playlistTitle: String): AddTrackToPlaylistState()

    data class TrackNotAdded(val playlistTitle: String): AddTrackToPlaylistState()

}