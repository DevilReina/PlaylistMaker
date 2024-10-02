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
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.utils.AndroidLogger
import com.example.playlistmaker.sharing.ExternalNavigator


object Creator {
    private val logger = AndroidLogger()

    // TracksInteractor использует репозиторий для поиска треков
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(), logger)
    }
    // SearchHistoryInteractor использует SharedPreferences из Application Context
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        val sharedPreferences = App.getAppContext().getSharedPreferences("SEARCH_PREFS", Context.MODE_PRIVATE)
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(sharedPreferences))
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
    // SettingsInteractor использует SettingsRepository
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
    // В Creator
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigator(context))
    }






    // TracksRepository использует сетевой клиент, инициализированный с помощью Retrofit
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(App.getAppContext())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }
    private fun getSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(App.getAppContext())
    }

}