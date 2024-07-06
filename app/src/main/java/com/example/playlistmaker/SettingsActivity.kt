package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences =
            getSharedPreferences("com.example.playlistmaker_preferences", MODE_PRIVATE)
        val darkModeSwitch = findViewById<Switch>(R.id.dark_mode_switch)
        darkModeSwitch.isChecked = sharedPreferences.getBoolean("DARK_MODE", false)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply()
        }

        val backButton = findViewById<View>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
    }
    }
