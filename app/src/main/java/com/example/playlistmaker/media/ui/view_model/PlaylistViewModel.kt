package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.model.PlaylistsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    init {
        getPlaylists()
    }

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getAllPlaylists().collect{
                if(it.isEmpty()) stateLiveData.postValue(PlaylistsState.NoPlaylists)
                else stateLiveData.postValue(PlaylistsState.Content(it))
            }
        }
    }
}