package com.example.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override fun switchTheme(isDarkTheme: Boolean) {
        // Сохраняем выбранную тему в SharedPreferences
        val sharedPreferences = context.getSharedPreferences("THEME_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("DARK_THEME", isDarkTheme).apply()
    }

    override fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = context.getSharedPreferences("THEME_PREFS", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("DARK_THEME", false)
    }
}