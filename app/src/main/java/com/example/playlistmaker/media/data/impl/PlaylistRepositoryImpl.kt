package com.example.playlistmaker.media.data.impl

import android.util.Log
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.db.TrackInPlaylistEntity
import com.example.playlistmaker.media.db.dao.TrackInPlaylistDao
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
    private val trackInPlaylistDao: TrackInPlaylistDao,
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

                val trackEntity = mapTrackToTrackInPlaylistEntity(track)
                trackInPlaylistDao.insertTrack(trackEntity)
            }
        }
    }
    override suspend fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> = flow {
        appDatabase.playlistDao().getTracksForPlaylist(playlistId).collect { playlistEntity ->
            if (playlistEntity?.tracks != null) {
                val trackIds = deserializeTracks(playlistEntity.tracks)
                val tracks = trackIds.mapNotNull { trackId ->
                    trackInPlaylistDao.getTrackById(trackId.toInt())?.let { entity ->
                        mapTrackInPlaylistEntityToTrack(entity)
                    }
                }
                emit(tracks)
            } else {
                emit(emptyList())
            }
        }
    }
    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        Log.d("Debug", "getPlaylistById called with playlistId: $playlistId")
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        Log.d("Debug", "Fetched PlaylistEntity: $playlistEntity")
        return playlistEntity?.let { playlistConverter.map(it) }
    }

    override suspend fun deletePlaylistById(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlist.id ?: 0)
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        playlist?.let {
            // Преобразуем строку tracks обратно в список trackId
            val trackListType = object : TypeToken<List<Int>>() {}.type
            val trackIds: MutableList<Int> = Gson().fromJson(it.tracks, trackListType) ?: mutableListOf()

            // Удаляем trackId
            trackIds.remove(track.trackId)

            // Преобразуем обратно в строку
            val updatedTracks = Gson().toJson(trackIds)

            val updatedPlaylist = it.copy(
                tracks = updatedTracks,
                numberOfTracks = trackIds.size.toLong() // Обновляем количество треков
            )

            // Обновляем плейлист
            updatePlaylist(updatedPlaylist)

            // Проверяем, используется ли трек в других плейлистах
            val trackCount  = appDatabase.trackInPlaylistDao().getTrackById(track.trackId)

            // Если трек больше нигде не используется, удаляем его из базы
            if (trackCount == null) {
                appDatabase.trackInPlaylistDao().deleteTrack(track.trackId)
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

    // Маппинг трека в сущность таблицы `track_in_playlist`
    private fun mapTrackToTrackInPlaylistEntity(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId = track.trackId,
            trackTimestamp = System.currentTimeMillis(),
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
    // Маппинг TrackInPlaylistEntity в Track
    private fun mapTrackInPlaylistEntityToTrack(entity: TrackInPlaylistEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }
}
