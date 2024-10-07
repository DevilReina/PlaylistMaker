package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.SharingRepository


class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    override fun shareApp() {
        val appLink = repository.getShareAppLink()
        repository.shareApp(appLink)
    }

    override fun openTerms() {
        val termsLink = repository.getTermsLink()
        repository.openTerms(termsLink)
    }

    override fun openSupport() {
        val emailData = repository.getSupportEmailData()
        repository.openSupport(emailData)
    }
}
