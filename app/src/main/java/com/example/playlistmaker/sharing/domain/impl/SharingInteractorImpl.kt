package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.SharingRepository


class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    override fun shareApp() {
        repository.shareApp(repository.getShareAppLink())
    }

    override fun openTerms() {
        repository.openTerms(repository.getTermsLink())
    }

    override fun openSupport() {
        repository.openSupport(repository.getSupportEmailData())
    }
}
