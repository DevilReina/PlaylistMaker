package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.model.ThemeSettings
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.provideFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Устанавливаем начальное состояние темы
        val isDarkThemeEnabled = settingsViewModel.getThemeSettings().isDarkTheme
        binding.themeSwitcher.isChecked = isDarkThemeEnabled

        // Обработчик переключения темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.updateThemeSettings(ThemeSettings(isChecked))
        }

        // Подписываемся на изменения темы
        settingsViewModel.themeChanged.observe(this) { _ -> }

        // Обработчики других кнопок
        binding.shareAppButton.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.contactSupportButton.setOnClickListener {
            settingsViewModel.openSupport()
        }

        binding.userAgreementButton.setOnClickListener {
            settingsViewModel.openTerms()
        }

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
