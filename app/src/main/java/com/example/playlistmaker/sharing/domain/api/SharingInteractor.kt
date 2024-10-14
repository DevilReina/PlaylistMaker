package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.model.EmailData

interface SharingInteractor {
    fun shareApp(): String
    fun openTerms(): String
    fun openSupport(): EmailData
}
