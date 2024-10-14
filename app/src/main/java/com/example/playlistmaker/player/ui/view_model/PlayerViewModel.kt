package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> get() = _currentPosition

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val position = playerInteractor.getCurrentPosition()
            _currentPosition.postValue(formatTime(position))
            handler.postDelayed(this, UPDATE_DELAY)
        }
    }

    init {
        _playerState.value = PlayerState.Default
    }

    fun preparePlayer(url: String?) {
        url?.let {
            playerInteractor.preparePlayer(it, onReady = {
                _playerState.postValue(PlayerState.Prepared)
            }, onComplete = {
                _playerState.postValue(PlayerState.Prepared)
                handler.removeCallbacks(updateTimeRunnable)
                _currentPosition.postValue(formatTime(0))
            })
        }
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.value = PlayerState.Playing
        handler.post(updateTimeRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playerState.value = PlayerState.Paused
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun formatTime(timeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}


