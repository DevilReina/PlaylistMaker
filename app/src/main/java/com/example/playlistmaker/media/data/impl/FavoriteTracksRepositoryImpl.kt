package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackConverter: TrackDbConvertor,
) : FavoriteTracksRepository {

    override suspend fun addFavoriteTrack(track: Track) {

        val trackEntity = trackConverter.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)

    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        if (isTrackFavorite(track.trackId)) {
            appDatabase.trackDao().deleteTrack(track.trackId)
        }
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        val existingTrackId = appDatabase.trackDao().getIdTracks()
        return existingTrackId.contains(trackId)
    }

    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track ->
            trackConverter.map(track)
        }
    }
}
