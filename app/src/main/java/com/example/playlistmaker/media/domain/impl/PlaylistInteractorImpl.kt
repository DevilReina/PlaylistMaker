package com.example.playlistmaker.media.domain.impl

import android.util.Log
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
        Log.d("PlaylistInteractor", "Added playlist: $playlist")
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }


    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()

    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return playlistRepository.getTracksForPlaylist(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun deletePlaylistById(playlistId: Playlist) {
        return playlistRepository.deletePlaylistById(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long) {
        playlistRepository.removeTrackFromPlaylist(track, playlistId)
    }

}
