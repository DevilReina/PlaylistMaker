package com.example.playlistmaker.sharing.domain.api

import android.content.Intent

interface SharingRepository {
    fun createShareIntent(shareMessage: String): Intent
}