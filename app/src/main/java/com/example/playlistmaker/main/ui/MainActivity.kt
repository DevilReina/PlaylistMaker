package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.main.ui.view_model.MainViewModel
import com.example.playlistmaker.media.ui.MediaLibraryActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Инициализация ViewModel с использованием фабрики
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.getMainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Устанавливаем обработчики для кнопок через ViewBinding и ViewModel
        binding.searchButton.setOnClickListener {
            mainViewModel.openActivity(SearchActivity::class.java)
        }

        binding.mediaLibraryButton.setOnClickListener {
            mainViewModel.openActivity(MediaLibraryActivity::class.java)
        }

        binding.settingsButton.setOnClickListener {
            mainViewModel.openActivity(SettingsActivity::class.java)
        }
    }
}
