package com.example.playlistmaker.support.domain

import android.content.Intent

interface SupportInteractor {
    fun createSupportEmailIntent(email: String, subject: String, body: String): Intent
}