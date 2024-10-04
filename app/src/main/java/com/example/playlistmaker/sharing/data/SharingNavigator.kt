package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.model.EmailData

interface SharingNavigator {
    fun shareApp(appLink: String)
    fun openTerms(termsLink: String)
    fun openSupport(emailData: EmailData)
}
