package com.example.playlistmaker.policy.domain

import android.content.Intent

interface PolicyInteractor {
    fun createPolicyIntent(url: String): Intent
}