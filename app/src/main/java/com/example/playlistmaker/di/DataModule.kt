package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.db.AppDatabase
import com.example.playlistmaker.media.db.AppDatabase.Companion.MIGRATION_4_5
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.utils.SEARCH_PREFS
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val iTunesBaseUrl = "https://itunes.apple.com"

val dataModule = module {

    single<ApiService> {
        Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    factory {
        MediaPlayer()
    }

    single {
        androidContext()
            .getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.playlistmaker")
            .addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<AppDatabase>().trackInPlaylistDao()
    }

    factory {
        PlaylistDbConverter()
    }
}