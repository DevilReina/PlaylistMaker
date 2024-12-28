package com.example.playlistmaker.player.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _addTrackStatus = MutableLiveData<Boolean>()


    private var updateJob: Job? = null

    init {
        _playerState.value = PlayerState.Default
        getPlaylists()
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



    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Проверяем, есть ли трек в плейлисте
                val trackIds = playlist.tracks
                    ?.replace("[", "") // Убираем открывающую квадратную скобку
                    ?.replace("]", "") // Убираем закрывающую квадратную скобку
                    ?.split(",")       // Разделяем строку по запятым
                    ?.mapNotNull { it.trim().toLongOrNull() } // Преобразуем в Long
                    ?: emptyList() // Если список пустой, возвращаем пустой список
                if (trackIds.contains(track.trackId.toLong())) {
                    // Трек уже есть в плейлисте
                    _addTrackStatus.postValue(false)
                    return@withContext
                }

                try {
                    // Добавляем трек в плейлист через интерактор
                    playlistInteractor.addTrackToPlaylist(track, playlist)

                    getPlaylists()
                    _addTrackStatus.postValue(true)
                } catch (e: Exception) {
                    _addTrackStatus.postValue(false)
                }
            }
        }
    }
    fun getPlaylistsMutableData(): MutableLiveData<List<Playlist>> {
        return playlistsLiveData
    }


    companion object {
        private const val UPDATE_DELAY_MILLS = 300L
    }
}
