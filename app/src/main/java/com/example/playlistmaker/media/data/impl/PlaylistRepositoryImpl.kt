package com.example.playlistmaker.media.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream


class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistConverter: PlaylistDbConverter,
    private val context: Context,
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


    override suspend fun saveImageToPrivateStorage(uri: String): String {

        val uriParsed = uri.toUri()

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PlaylistMakerAlbum")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val fileName = System.currentTimeMillis()

        val file = File(filePath, "$fileName.jpg")

        val inputStream = context.contentResolver.openInputStream(uriParsed)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)


        return file.toString()

    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlist.id!!)
        playlistEntity?.let { existingPlaylist ->
            // Десериализация треков
            val tracksList: MutableList<Long> = deserializeTracks(existingPlaylist.tracks).toMutableList()

            // Добавление нового трека, если его ещё нет
            if (!tracksList.contains(track.trackId.toLong())) {
                tracksList.add(track.trackId.toLong())

                // Сериализация обновлённого списка треков
                val updatedTracks = serializeTracks(tracksList)

                // Создание обновлённого плейлиста
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
