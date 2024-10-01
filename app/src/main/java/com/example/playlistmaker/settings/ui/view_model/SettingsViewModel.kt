package com.example.playlistmaker.settings.ui.view_model

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.policy.domain.PolicyInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.support.domain.SupportInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
    private val supportInteractor: SupportInteractor,
    private val policyInteractor: PolicyInteractor
) : ViewModel() {

    // LiveData для отслеживания текущей темы
    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    private val _shareIntent = MutableLiveData<Intent>()
    val shareIntent: LiveData<Intent> get() = _shareIntent

    private val _emailIntent = MutableLiveData<Intent>()
    val emailIntent: LiveData<Intent> get() = _emailIntent

    private val _policyIntent = MutableLiveData<Intent>()
    val policyIntent: LiveData<Intent> get() = _policyIntent

    init {
        // Инициализируем тему при создании ViewModel
        _isDarkTheme.value = settingsInteractor.isDarkThemeEnabled()
    }

    // Метод для переключения темы
    fun switchTheme(isDark: Boolean) {
        // Сохраняем тему через интерактор
        settingsInteractor.switchTheme(isDark)

        // Переключаем тему с помощью AppCompatDelegate
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        // Обновляем LiveData
        _isDarkTheme.value = isDark
    }

    //метод шаринга
    fun shareApp(shareMessage: String) {
        val intent = sharingInteractor.createShareIntent(shareMessage)
        _shareIntent.value = intent
    }

    // Метод для отправки email
    fun sendSupportEmail(email: String, subject: String, body: String) {
        _emailIntent.value = supportInteractor.createSupportEmailIntent(email, subject, body)
    }

    // Метод для открытия политики конфиденциальности
    fun openPolicy(url: String) {
        _policyIntent.value = policyInteractor.createPolicyIntent(url)
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Получаем SettingsInteractor через Creator
                val settingsInteractor = Creator.provideSettingsInteractor()
                val sharingInteractor = Creator.provideSharingInteractor()
                val supportInteractor = Creator.provideSupportInteractor()
                val policyInteractor = Creator.providePolicyInteractor()

                // Возвращаем новую инстанцию ViewModel
                SettingsViewModel(settingsInteractor, sharingInteractor, supportInteractor, policyInteractor)
            }
        }
    }
}
