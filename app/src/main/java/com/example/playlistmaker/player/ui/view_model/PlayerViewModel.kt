package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.media.model.AddTrackToPlaylistState
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
    ) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun getPlaylistsLiveData(): LiveData<List<Playlist>> = playlistsLiveData

    private val addTrackToPlaylistLiveData = MutableLiveData<AddTrackToPlaylistState>()
    fun getAddTrackToPlaylistLiveData(): LiveData<AddTrackToPlaylistState> = addTrackToPlaylistLiveData

    private var updateJob: Job? = null

    init {
        getPlaylists()

        _playerState.value = PlayerState.Default
    }

    fun onFavoriteClicked(track: Track) {

        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoriteTracksInteractor.deleteFavoriteTrack(track)
                _isFavorite.postValue(false)
            } else {
                favoriteTracksInteractor.addFavoriteTrack(track)
                _isFavorite.postValue(true)
            }
        }
    }
    fun loadTrack(track: Track) {
        viewModelScope.launch {
            val isTrackFavorite = favoriteTracksInteractor.isTrackFavorite(track.trackId)
            _isFavorite.postValue(isTrackFavorite)
        }
    }

    fun preparePlayer(url: String?) {
        url?.let {
            playerInteractor.preparePlayer(it, onReady = {
                _playerState.postValue(PlayerState.Prepared)
            }, onComplete = {
                stopPositionUpdates()
                _playerState.postValue(PlayerState.Prepared)
            })
        }
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.value = PlayerState.Playing(formatTime(playerInteractor.getCurrentPosition()))
        startPositionUpdates()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playerState.value = PlayerState.Paused(formatTime(playerInteractor.getCurrentPosition()))
        stopPositionUpdates()
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        stopPositionUpdates()
    }

    private fun startPositionUpdates() {
        stopPositionUpdates() // Остановка любых предыдущих задач
        updateJob = viewModelScope.launch {
            while (true) {
                val position = playerInteractor.getCurrentPosition()
                _playerState.postValue(PlayerState.Playing(formatTime(position)))
                delay(UPDATE_DELAY_MILLS)
            }
        }
    }

    private fun stopPositionUpdates() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun formatTime(timeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getAllPlaylists().collect{
                if(it.isEmpty()) playlistsLiveData.postValue(listOf())
                else playlistsLiveData.postValue(it)
            }
        }
    }

    private var currentTrack: Track? = null

    fun setTrack(track: Track) {
        currentTrack = track
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        // Проверяем, есть ли доступный трек
        val track = currentTrack ?: return // Если трек не найден, выходим из метода

        val itemType = object : TypeToken<ArrayList<String>>() {}.type
        var listOfTracks = Gson().fromJson<ArrayList<String>>(playlist.tracks, itemType)

        if (listOfTracks.isNullOrEmpty()) listOfTracks = ArrayList()

        if (listOfTracks.contains(track.trackId.toString())) {
            addTrackToPlaylistLiveData.value = AddTrackToPlaylistState.TrackNotAdded(playlist.title)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                if (!listOfTracks.contains(track.trackId.toString())) {
                    // Добавляем трек в плейлист
                    playlistInteractor.addTrackToPlaylist(track, playlist)
                    addTrackToPlaylistLiveData.postValue(AddTrackToPlaylistState.TrackAdded(playlist.title))

                    // Обновляем список плейлистов
                    getPlaylists()
                } else {
                    addTrackToPlaylistLiveData.postValue(
                        AddTrackToPlaylistState.TrackNotAdded(playlist.title)
                    )
                }
                addTrackToPlaylistLiveData.postValue(AddTrackToPlaylistState.Default)
            }
        }
    }



    companion object {
        private const val UPDATE_DELAY_MILLS = 300L
    }
}
