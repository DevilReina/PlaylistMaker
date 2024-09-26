package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun preparePlayer(url: String?, onReady: () -> Unit, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
}