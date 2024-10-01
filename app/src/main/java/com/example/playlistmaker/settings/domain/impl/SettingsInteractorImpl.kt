package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val themeRepository: SettingsRepository) : SettingsInteractor {

    override fun switchTheme(isDark: Boolean) {
        themeRepository.switchTheme(isDark)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }
}