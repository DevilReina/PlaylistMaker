package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String?, onReady: () -> Unit, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
}
