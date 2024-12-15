package com.example.playlistmaker.media.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.model.FavoriteTracksState
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val context: Context,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
    ): ViewModel() {

    private val favoriteTracksState = MutableLiveData<FavoriteTracksState>()

    fun observeState(): LiveData<FavoriteTracksState> = favoriteTracksState

    fun updateFavoriteTracks() {
        renderState(FavoriteTracksState.Loading)
        viewModelScope.launch {
            favoriteTracksInteractor.favoriteTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    fun removeTrackFromFavorites(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor.deleteFavoriteTrack(track)
            updateFavoriteTracks()
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteTracksState.Empty(context.getString(R.string.text_error)))
        } else {
            val sortedTracks = tracks.sortedByDescending { it.trackTimestamp }
            renderState(FavoriteTracksState.Content(sortedTracks))
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        favoriteTracksState.postValue(state)
    }
}