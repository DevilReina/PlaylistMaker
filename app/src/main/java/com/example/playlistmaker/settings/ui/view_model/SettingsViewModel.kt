package com.example.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
        _themeChanged.postValue(themeSettings.isDarkTheme)
    }

    // Шаринг приложения через sharingInteractor
    fun shareApp() {
        sharingInteractor.shareApp()
    }

    // Открытие пользовательских соглашений через sharingInteractor
    fun openTerms() {
        sharingInteractor.openTerms()
    }

    // Открытие поддержки через sharingInteractor
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
