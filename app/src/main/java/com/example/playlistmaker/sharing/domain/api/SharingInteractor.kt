package com.example.playlistmaker.sharing.domain.api

import android.content.Intent

interface SharingInteractor {
    fun createShareIntent(shareMessage: String): Intent
}