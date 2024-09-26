package com.example.playlistmaker.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor
    private var isDarkThemeEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Получаем интерактор для работы с темой
        settingsInteractor = Creator.provideSettingsInteractor()

        // Получаем текущее состояние темы
        isDarkThemeEnabled = settingsInteractor.isDarkThemeEnabled()

        // Свитчер темы
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = isDarkThemeEnabled

        // Обработчик переключения темы
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != isDarkThemeEnabled) {
                isDarkThemeEnabled = isChecked
                settingsInteractor.switchTheme(isChecked)
                switchTheme(isChecked) // Меняем тему
            }
        }

            val shareButton = findViewById<View>(R.id.share_app_button)
            shareButton.setOnClickListener {
                val message = getString(R.string.share_btn)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                startActivity(shareIntent)
            }


            val supportButton = findViewById<View>(R.id.contact_support_button)
            supportButton.setOnClickListener {
                val email = getString(R.string.support_email)
                val text = getString(R.string.support_email_body)
                val subject = getString(R.string.support_email_subject)

                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                intent.putExtra(Intent.EXTRA_TEXT, text)
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                startActivity(intent)
            }

            val policyButton = findViewById<View>(R.id.user_agreement_button)
            policyButton.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.user_agreement_url))
                    )
                )
            }

        val backButton = findViewById<View>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Закрываем активность по нажатию на "Назад"
        }
    }
    private fun switchTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}