package com.example.playlistmaker

import android.app.Application

class App:Application() {

    companion object {
        const val TRACK_DT = "TRACK"
        lateinit var instance: App
            private set
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }



}