package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteRepository: FavoriteTracksRepository
): FavoriteTracksInteractor {

    override suspend fun addFavoriteTrack(track: Track) {
        favoriteRepository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoriteRepository.deleteFavoriteTrack(track)
    }

    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.favoriteTracks()
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return favoriteRepository.isTrackFavorite(trackId)
    }
}