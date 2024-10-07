package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.model.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
    fun shareApp(appLink: String)
    fun openTerms(termsLink: String)
    fun openSupport(emailData: EmailData)
}
