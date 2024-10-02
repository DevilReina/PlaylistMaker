package com.example.playlistmaker.sharing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.model.EmailData

class ExternalNavigator(private val context: Context) {

    // Метод для получения контекста
    fun getContext(): Context = context

    // Метод для отправки ссылки на приложение
    fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    // Метод для открытия ссылки в браузере
    @SuppressLint("QueryPermissionsNeeded")
    fun openLink(url: String) {
        val linkIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (linkIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(linkIntent)
        }
    }

    // Метод для отправки email
    @SuppressLint("QueryPermissionsNeeded")
    fun openEmail(emailData: EmailData) {
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
