package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.utils.DARK_THEME
import com.example.playlistmaker.utils.THEME_PREFS
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppDatabase.getDatabase(this)
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        // Применяем тему при запуске приложения
        applySavedTheme()


    }

    private fun applySavedTheme() {
        val settingsInteractor: SettingsInteractor by inject()
        val sharedPreferences = getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)

        if (!sharedPreferences.contains(DARK_THEME)) {
            // Используем системную тему по умолчанию
            val isDarkTheme = resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean(DARK_THEME, isDarkTheme).apply()
        }

        val themeSettings = settingsInteractor.getThemeSettings()
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