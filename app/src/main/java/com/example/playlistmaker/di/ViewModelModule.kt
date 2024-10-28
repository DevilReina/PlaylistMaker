package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get())
    }

}