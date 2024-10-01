package com.example.playlistmaker.sharing.data

import android.content.Intent
import com.example.playlistmaker.sharing.domain.api.SharingRepository

class SharingRepositoryImpl : SharingRepository {

    override fun createShareIntent(shareMessage: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
    }
}