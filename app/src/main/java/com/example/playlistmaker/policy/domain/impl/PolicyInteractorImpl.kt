package com.example.playlistmaker.policy.domain.impl

import android.content.Intent
import com.example.playlistmaker.policy.domain.PolicyInteractor

class PolicyInteractorImpl : PolicyInteractor {

    override fun createPolicyIntent(url: String): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(url)
        }
    }
}