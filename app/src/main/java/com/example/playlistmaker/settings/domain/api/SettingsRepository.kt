package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun switchTheme(isDarkTheme: Boolean)
    fun isDarkThemeEnabled(): Boolean
}