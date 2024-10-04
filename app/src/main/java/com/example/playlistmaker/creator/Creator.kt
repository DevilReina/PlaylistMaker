package com.example.playlistmaker.creator


import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.SharingNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.data.impl.SharingNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.utils.SEARCH_PREFS


object Creator {

    // TracksInteractor использует репозиторий для поиска треков
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
    // SearchHistoryInteractor использует SharedPreferences из Application Context
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        val sharedPreferences = App.getAppContext().getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE)
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(sharedPreferences))
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
    // SettingsInteractor использует SettingsRepository
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun provideSharingNavigator(context: Context): SharingNavigator {
        return SharingNavigatorImpl(context)
    }

    fun provideSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val repository = provideSharingRepository(context)
        val navigator = provideSharingNavigator(context)
        return SharingInteractorImpl(repository, navigator)
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

}