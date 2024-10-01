package com.example.playlistmaker.sharing.domain.impl

import android.content.Intent
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.SharingRepository

class SharingInteractorImpl(private val repository: SharingRepository) : SharingInteractor {

    override fun createShareIntent(shareMessage: String): Intent {
        // Используем репозиторий для создания Intent
        return repository.createShareIntent(shareMessage)
    }
}