package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun deletePlaylistById(playlistId: Playlist)

    suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long)

}