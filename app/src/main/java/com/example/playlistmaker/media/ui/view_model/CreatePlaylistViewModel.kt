package com.example.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val imageUri = MutableLiveData<String>()
    fun saveImageUri(uri: String) {
        imageUri.value = uri
        Log.d("CreatePlaylistViewModel", "Image URI saved: $uri") // Логируем URI
    }

    private val imageIsSetLiveData = MutableLiveData(false)
    fun getImageIsSetLiveData(): LiveData<Boolean> = imageIsSetLiveData

    private val titleIsSetLiveData = MutableLiveData(false)
    fun getTitleIsSetLiveData(): LiveData<Boolean> = titleIsSetLiveData

    private val descriptionIsSetLiveData = MutableLiveData(false)
    fun getDescriptionIsSetLiveData(): LiveData<Boolean> = descriptionIsSetLiveData

    fun setImage(isSet: Boolean) {
        imageIsSetLiveData.value = isSet
    }

    fun setTitle(isSet: Boolean) {
        titleIsSetLiveData.value = isSet
    }

    fun setDescription(isSet: Boolean) {
        descriptionIsSetLiveData.value = isSet
    }

    fun createPlaylist(title: String, description: String) {

        viewModelScope.launch(Dispatchers.IO) {

            val savedUri = if(imageUri.value.isNullOrEmpty()) ""
            else playlistInteractor.saveImageToPrivateStorage(imageUri.value!!)

            playlistInteractor.createPlaylist(
                Playlist(title = title,
                description = description,
                imageUri = savedUri,
                tracks = "",
                numberOfTracks = 0)
            )
        }
    }

}