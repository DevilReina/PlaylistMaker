package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.sharing.model.EmailData

class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    override fun shareApp(): String {
        return repository.getShareAppLink()
    }

    override fun openTerms(): String {
        return repository.getTermsLink()
    }

    override fun openSupport(): EmailData {
        return repository.getSupportEmailData()
    }
}
