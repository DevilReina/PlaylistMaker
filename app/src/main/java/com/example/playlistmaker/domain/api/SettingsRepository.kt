package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun saveTheme(isDarkThemeEnabled: Boolean)
    fun loadTheme(): Boolean
}