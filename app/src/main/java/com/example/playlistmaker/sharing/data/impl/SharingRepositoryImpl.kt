package com.example.playlistmaker.sharing.data.impl

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.sharing.model.EmailData

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun getShareAppLink(): String {
        // Получаем ссылку на приложение из ресурсов
        return context.getString(R.string.share_message)
    }

    override fun getTermsLink(): String {
        // Получаем ссылку на пользовательское соглашение из ресурсов
        return context.getString(R.string.user_agreement_url)
    }

    override fun getSupportEmailData(): EmailData {
        // Получаем данные для отправки email на поддержку из ресурсов
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_email_subject),
            body = context.getString(R.string.support_email_body)
        )
    }
    override fun shareApp(appLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.text_share)))
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
