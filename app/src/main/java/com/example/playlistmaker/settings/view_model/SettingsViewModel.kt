package com.example.playlistmaker.settings.view_model

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _themeChanged = MutableLiveData<Boolean>()
    val themeChanged: LiveData<Boolean> get() = _themeChanged

    private val _actionCommand = MutableLiveData<Intent?>()
    val actionCommand: LiveData<Intent?> get() = _actionCommand

    // Обновление настроек темы и применение
    fun updateThemeSettings(themeSettings: ThemeSettings) {
        settingsInteractor.updateThemeSettings(themeSettings)
        _themeChanged.postValue(themeSettings.isDarkTheme)
    }

    fun shareApp() {
        val appLink = sharingInteractor.shareApp()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        _actionCommand.postValue(Intent.createChooser(shareIntent, "Share via"))
    }

    fun openTerms() {
        val termsLink = sharingInteractor.openTerms()
        val termsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsLink)
        }
        _actionCommand.postValue(termsIntent)
    }

    fun openSupport() {
        val emailData = sharingInteractor.openSupport()
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
        _actionCommand.postValue(emailIntent)
    }
    fun clearActionCommand() {
        _actionCommand.postValue(null)
    }
    // Получение текущих настроек темы
    fun getThemeSettings(): ThemeSettings {
        return settingsInteractor.getThemeSettings()
    }

}
