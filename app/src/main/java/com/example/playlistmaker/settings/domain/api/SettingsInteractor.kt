package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}
