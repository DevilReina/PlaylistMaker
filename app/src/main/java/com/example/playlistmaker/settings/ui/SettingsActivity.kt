package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.model.ThemeSettings
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Устанавливаем начальное состояние темы
        val isDarkThemeEnabled = viewModel.getThemeSettings().isDarkTheme
        binding.themeSwitcher.isChecked = isDarkThemeEnabled

        // Обработчик переключения темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateThemeSettings(ThemeSettings(isChecked))
        }

        // Подписываемся на изменения темы
        viewModel.themeChanged.observe(this) { _ -> }

        // Обработчики других кнопок через ViewModel
        binding.shareAppButton.setOnClickListener {
            viewModel.shareApp()
        }

        binding.contactSupportButton.setOnClickListener {
            viewModel.openSupport()
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewModel.actionCommand.observe(this) { intent ->
            intent?.let { startActivity(it) }
        }
    }

}

