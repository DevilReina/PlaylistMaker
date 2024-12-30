package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun createPlaylist(imageUri: String?, title: String, description: String?){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playlistInteractor.createPlaylist(Playlist(0, title, description, imageUri, null, 0))
            }
        }
    }
}