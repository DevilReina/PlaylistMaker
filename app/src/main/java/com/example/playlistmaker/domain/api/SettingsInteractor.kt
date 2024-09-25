package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun switchTheme(isDarkThemeEnabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
}