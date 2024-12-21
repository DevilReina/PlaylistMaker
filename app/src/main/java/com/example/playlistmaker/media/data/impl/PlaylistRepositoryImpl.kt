package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.db.PlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase
) {

    suspend fun addPlaylist(playlist: PlaylistEntity) {
        appDatabase.playlistDao().addPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: PlaylistEntity) {
        appDatabase.playlistDao().updatePlaylist(playlist)
    }

    fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return flow {
            emit(appDatabase.playlistDao().getAllPlaylists())
        }
    }

    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity? {
        return appDatabase.playlistDao().getPlaylistById(playlistId)
    }
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        playlist?.let {
            val updatedTracks = it.tracks.toMutableList().apply {
                add(trackId)
            }
            val updatedPlaylist = it.copy(tracks = updatedTracks, numberOfTracks = updatedTracks.size.toLong())
            appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
        }
    }

}

