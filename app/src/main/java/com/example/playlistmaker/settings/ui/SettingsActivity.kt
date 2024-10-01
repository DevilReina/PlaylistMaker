package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels { SettingsViewModel.provideFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val shareButton = findViewById<View>(R.id.share_app_button)
        val supportButton = findViewById<View>(R.id.contact_support_button)
        val policyButton = findViewById<View>(R.id.user_agreement_button)

        // viewModel LiveData для темы
        viewModel.isDarkTheme.observe(this) { isDarkTheme ->
            themeSwitcher.isChecked = isDarkTheme
        }
        // viewModel LiveData sharing для запуска  share Intent
        viewModel.shareIntent.observe(this) { intent ->
            startActivity(Intent.createChooser(intent, null)) // Запускаем Intent через Activity
        }
        // viewModel LiveData для запуска email Intent
        viewModel.emailIntent.observe(this) { intent ->
            startActivity(Intent.createChooser(intent, null))
        }
        // viewModel LiveData для запуска policy Intent
        viewModel.policyIntent.observe(this) { intent ->
            startActivity(intent)
        }

        // Обработка изменения состояния свитчера
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked) // Меняем тему через SettingsViewModel
        }

        shareButton.setOnClickListener {
            val message = getString(R.string.share_btn)
            viewModel.shareApp(message) // Шарим приложение через SettingsViewModel
        }

        supportButton.setOnClickListener {
            val email = getString(R.string.support_email)
            val text = getString(R.string.support_email_body)
            val subject = getString(R.string.support_email_subject)
            viewModel.sendSupportEmail(email, text, subject) // Отправляем email через SettingsViewModel
        }

        policyButton.setOnClickListener {
            val policyUrl = getString(R.string.user_agreement_url)
            viewModel.openPolicy(policyUrl) // Открываем политику через ViewModel
        }

        val backButton = findViewById<View>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Закрываем активность по нажатию на "Назад"
        }
    }
}