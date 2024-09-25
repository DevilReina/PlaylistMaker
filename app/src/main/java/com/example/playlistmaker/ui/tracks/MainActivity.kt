package com.example.playlistmaker.ui.tracks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.MediaLibraryActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.SearchActivity
import com.example.playlistmaker.ui.SettingsActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        // Применяем тему перед созданием активности
        val settingsInteractor = Creator.provideSettingsInteractor(applicationContext)
        val isDarkThemeEnabled = settingsInteractor.isDarkThemeEnabled()
        settingsInteractor.switchTheme(isDarkThemeEnabled)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val searchButton = findViewById<LinearLayout>(R.id.search_button)
        val mediaLibraryButton = findViewById<LinearLayout>(R.id.media_library_button)
        val settingsButton = findViewById<LinearLayout>(R.id.settings_button)

        // Анонимный класс для первой кнопки
        val searchClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        searchButton.setOnClickListener(searchClickListener)

        // Лямбда-выражение для второй кнопки
        mediaLibraryButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MediaLibraryActivity::class.java)
            startActivity(intent)
        }

        // onClick для третьей кнопки через xml атрибут
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
