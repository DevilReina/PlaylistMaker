package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.SharingRepository


class SharingInteractorImpl(
    private val repository: SharingRepository,
    private val navigator: SharingRepository
) : SharingInteractor {

    override fun shareApp() {
        val appLink = repository.getShareAppLink()
        navigator.shareApp(appLink)
    }

    override fun openTerms() {
        val termsLink = repository.getTermsLink()
        navigator.openTerms(termsLink)
    }

    override fun openSupport() {
        val emailData = repository.getSupportEmailData()
        navigator.openSupport(emailData)
    }
}
