package com.example.playlistmaker.media.data.impl

import android.content.Context
import android.util.Log
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistConverter: PlaylistDbConverter,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        Log.d("START SAVE", "START Saving playlist: $playlist")
        appDatabase.playlistDao().addPlaylist(playlistConverter.map(playlist))
        Log.d("PlaylistRepository", "Saving playlist: $playlist")
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistConverter.map(playlist))
    }

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists().map {
            playlistConverter.map(it)
        }
        emit(playlists)
        Log.d("PlaylistRepository", "Retrieved playlists: $playlists")
    }


    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlist.id!!)
        playlistEntity?.let { existingPlaylist ->
            val tracksList: MutableList<Long> = if (existingPlaylist.tracks.isNullOrEmpty()) {
                mutableListOf()
            } else {
                deserializeTracks(existingPlaylist.tracks).toMutableList()
            }

            // Добавляем трек, если его ещё нет
            if (!tracksList.contains(track.trackId.toLong())) {
                tracksList.add(track.trackId.toLong())

                val updatedTracks = serializeTracks(tracksList)

                val updatedPlaylist = existingPlaylist.copy(
                    tracks = updatedTracks,
                    numberOfTracks = tracksList.size.toLong()
                )
                appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
            }
        }
    }

    // Сериализация списка треков в строку
    private fun serializeTracks(tracks: List<Long>): String {
        return gson.toJson(tracks)
    }

    // Десериализация строки в список треков
    private fun deserializeTracks(tracks: String): List<Long> {
        return gson.fromJson(tracks, object : TypeToken<List<Long>>() {}.type) ?: emptyList()
    }
}
