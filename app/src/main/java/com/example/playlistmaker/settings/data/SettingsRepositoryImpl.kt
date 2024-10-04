package com.example.playlistmaker.settings.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.model.ThemeSettings
import com.example.playlistmaker.utils.DARK_THEME
import com.example.playlistmaker.utils.THEME_PREFS

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val sharedPreferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean(DARK_THEME, false)
        return ThemeSettings(isDarkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        val sharedPreferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(DARK_THEME, settings.isDarkTheme).apply()
        // Применяем тему
        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
