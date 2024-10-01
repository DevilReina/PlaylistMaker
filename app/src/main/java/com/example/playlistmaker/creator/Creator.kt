package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.policy.domain.PolicyInteractor
import com.example.playlistmaker.policy.domain.impl.PolicyInteractorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.support.domain.SupportInteractor
import com.example.playlistmaker.support.domain.impl.SupportInteractorImpl
import com.example.playlistmaker.utils.AndroidLogger

object Creator {
    private val logger = AndroidLogger()

    // TracksInteractor использует репозиторий для поиска треков
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(), logger)
    }
    // SettingsInteractor использует SettingsRepository
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getThemeRepository())
    }
    // SearchHistoryInteractor использует SharedPreferences из Application Context
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        val sharedPreferences = App.getAppContext().getSharedPreferences("SEARCH_PREFS", Context.MODE_PRIVATE)
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(sharedPreferences))
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
    // SharingInteractor использует SharingRepository
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(SharingRepositoryImpl())
    }
    // SupportInteractor
    fun provideSupportInteractor(): SupportInteractor {
        return SupportInteractorImpl()
    }
    // PolicyInteractor
    fun providePolicyInteractor(): PolicyInteractor {
        return PolicyInteractorImpl()
    }

    // TracksRepository использует сетевой клиент, инициализированный с помощью Retrofit
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getThemeRepository(): SettingsRepository {
        return SettingsRepositoryImpl(App.getAppContext())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }


}