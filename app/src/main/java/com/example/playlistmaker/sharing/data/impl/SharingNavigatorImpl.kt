package com.example.playlistmaker.sharing.data.impl

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.SharingNavigator
import com.example.playlistmaker.sharing.model.EmailData

class SharingNavigatorImpl(private val context: Context) : SharingNavigator {

    override fun shareApp(appLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun openTerms(termsLink: String) {
        val linkIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsLink)
        }
        if (linkIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(linkIntent)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun openSupport(emailData: EmailData) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(emailIntent)
        }
    }
}
