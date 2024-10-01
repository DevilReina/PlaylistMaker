package com.example.playlistmaker.support.domain.impl

import android.content.Intent
import com.example.playlistmaker.support.domain.SupportInteractor

class SupportInteractorImpl : SupportInteractor {

    override fun createSupportEmailIntent(email: String, subject: String, body: String): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }
}