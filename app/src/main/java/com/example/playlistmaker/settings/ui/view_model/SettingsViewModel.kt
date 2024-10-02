package com.example.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _themeChanged = MutableLiveData<Boolean>()
    val themeChanged: LiveData<Boolean> get() = _themeChanged

    // Обновление настроек темы и применение
    fun updateThemeSettings(themeSettings: ThemeSettings) {
        settingsInteractor.updateThemeSettings(themeSettings)
        applyTheme(themeSettings.isDarkTheme)
    }

    // Применение темы через ViewModel
    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        _themeChanged.postValue(isDarkTheme)
    }

    // Шаринг приложения
    fun shareApp() {
        sharingInteractor.shareApp()
    }

    // Открытие пользовательских соглашений
    fun openTerms() {
        sharingInteractor.openTerms()
    }

    // Открытие поддержки
    fun openSupport() {
        sharingInteractor.openSupport()
    }

    // Получение текущих настроек темы
    fun getThemeSettings(): ThemeSettings {
        return settingsInteractor.getThemeSettings()
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sharingInteractor = Creator.provideSharingInteractor(context)
                val settingsInteractor = Creator.provideSettingsInteractor()
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }
}
