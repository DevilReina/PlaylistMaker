package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository


class SettingsInteractorImpl(private val themeRepository: SettingsRepository) : SettingsInteractor {

    override fun switchTheme(isDarkTheme: Boolean) {
        // Сохраняем состояние темы в репозитории
        if (isDarkTheme != themeRepository.isDarkThemeEnabled()) {
            themeRepository.switchTheme(isDarkTheme)
        }
    }

    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }
}