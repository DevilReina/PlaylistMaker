package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }
}