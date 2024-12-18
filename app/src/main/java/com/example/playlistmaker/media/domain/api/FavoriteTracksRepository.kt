package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    fun favoriteTracks(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: Int): Boolean
}