package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Применяем тему при запуске приложения
        applySavedTheme()
    }

    private fun applySavedTheme() {
        val settingsInteractor = Creator.provideSettingsInteractor()
        val themeSettings = settingsInteractor.getThemeSettings()

        // Применяем тему
        AppCompatDelegate.setDefaultNightMode(
            if (themeSettings.isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val TRACK_DT = "TRACK"
        lateinit var instance: App
            private set
        // Метод для получения Application Context
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}