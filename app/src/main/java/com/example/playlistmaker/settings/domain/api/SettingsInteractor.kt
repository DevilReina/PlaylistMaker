package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun switchTheme(isDarkThemeEnabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
}