package com.example.playlistmaker.main.ui.view_model

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    // Метод для навигации на другую активность
    fun <T : Activity> openActivity(activityClass: Class<T>) {
        val intent = Intent(application, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)
    }

    companion object {
        fun getMainViewModelFactory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(application)
            }
        }
    }
}
