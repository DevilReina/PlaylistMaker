package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {

    companion object {
        private const val DARK_THEME_KEY = "DARK_THEME"
    }

    override fun saveTheme(isDarkThemeEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, isDarkThemeEnabled)
            .apply()
    }

    override fun loadTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }
}