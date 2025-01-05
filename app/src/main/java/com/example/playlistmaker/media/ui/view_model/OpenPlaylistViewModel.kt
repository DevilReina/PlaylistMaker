package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.launch

class OpenPlaylistViewModel(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> get() = _playlist


    private val _playlistTracks = MutableLiveData<List<Track>?>()
    val playlistTracks: MutableLiveData<List<Track>?> = _playlistTracks

    fun loadPlaylistTracks(playlistId: Long) {
        viewModelScope.launch {
            repository.getTracksForPlaylist(playlistId)
                .collect { tracks ->
                    _playlistTracks.postValue(tracks)
                }
        }
    }
    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = repository.getPlaylistById(playlistId)
            if (playlist != null) {
                _playlist.postValue(playlist)
            } else {
                // Логирование или обработка ошибки
                _playlist.postValue(null)
            }
        }
    }
    fun removeTrackFromPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(track, playlistId)
            val updatedTracks = _playlistTracks.value?.toMutableList()?.apply {
                remove(track)
            }
            _playlistTracks.postValue(updatedTracks)
            loadPlaylist(playlistId) // Обновляем данные плейлиста
            loadPlaylistTracks(playlistId) // Обновляем список треков
        }
    }


    fun deleteCurrentPlaylist(playlistId: Playlist) {
        viewModelScope.launch {
                // Удаление из базы данных или API
                repository.deletePlaylistById(playlistId)

        }
    }
}