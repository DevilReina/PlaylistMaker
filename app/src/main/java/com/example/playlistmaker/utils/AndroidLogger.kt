package com.example.playlistmaker.utils

import android.util.Log
import com.example.playlistmaker.domain.logger.Logger


class AndroidLogger: Logger {
    override fun log(tag: String, message: String) {
        Log.d(tag, message) // Используем Log только здесь
    }
}