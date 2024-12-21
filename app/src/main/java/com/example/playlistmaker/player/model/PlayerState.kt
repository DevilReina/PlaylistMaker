package com.example.playlistmaker.player.model

sealed class PlayerState {
    object Default : PlayerState()
    object Prepared : PlayerState()
    data class Playing(val currentPosition: String) : PlayerState()
    data class Paused(val currentPosition: String) : PlayerState()
}