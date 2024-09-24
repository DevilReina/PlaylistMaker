package com.example.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val saveSwitch = (applicationContext as App)
        themeSwitcher.isChecked = saveSwitch.darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
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
