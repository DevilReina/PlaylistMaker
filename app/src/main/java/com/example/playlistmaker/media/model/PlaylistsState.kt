package com.example.playlistmaker.media.model

sealed interface PlaylistsState {
    data object NoPlaylists: PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ): PlaylistsState
}