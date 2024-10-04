package com.example.playlistmaker.sharing.data

import android.content.Context
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
}
