package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.model.EmailData
import com.example.playlistmaker.sharing.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        // Используем метод getShareAppLink() для получения ссылки на приложение и передаем ее ExternalNavigator
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        // Используем метод getTermsLink() для получения ссылки на условия использования и передаем ее ExternalNavigator
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        // Используем метод getSupportEmailData() для получения email данных и передаем их ExternalNavigator
        externalNavigator.openEmail(getSupportEmailData())
    }

    // Метод для получения ссылки на приложение из ресурсов
    private fun getShareAppLink(): String {
        return externalNavigator.getContext().getString(R.string.share_message)
    }

    // Метод для получения данных для email из ресурсов
    private fun getSupportEmailData(): EmailData {
        val email = externalNavigator.getContext().getString(R.string.support_email)
        val subject = externalNavigator.getContext().getString(R.string.support_email_subject)
        val body = externalNavigator.getContext().getString(R.string.support_email_body)
        return EmailData(email, subject, body)
    }

    // Метод для получения ссылки на условия использования из ресурсов
    private fun getTermsLink(): String {
        return externalNavigator.getContext().getString(R.string.user_agreement_url)
    }
}
