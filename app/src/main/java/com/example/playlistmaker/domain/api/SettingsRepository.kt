package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun switchTheme(isDarkTheme: Boolean)
    fun isDarkThemeEnabled(): Boolean
}