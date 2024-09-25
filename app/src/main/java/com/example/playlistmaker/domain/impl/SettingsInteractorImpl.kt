package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {

    override fun switchTheme(isDarkThemeEnabled: Boolean) {
        // Сохраняем состояние темы в репозитории
        settingsRepository.saveTheme(isDarkThemeEnabled)
        // Применяем новую тему
        applyTheme(isDarkThemeEnabled)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.loadTheme()
    }

    private fun applyTheme(isDarkThemeEnabled: Boolean) {
        // Применяем тему через AppCompatDelegate
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}