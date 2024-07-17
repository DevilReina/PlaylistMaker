package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App:Application() {

    companion object {
        private const val PREFS_NAME = "THEME_PREFS"
        private const val DARK_THEME_KEY = "DARK_THEME"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        darkTheme = loadTheme()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveTheme(darkThemeEnabled)
    }

    private fun loadTheme(): Boolean {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    private fun saveTheme(darkThemeEnabled: Boolean) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }
}