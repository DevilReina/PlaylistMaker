package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<LinearLayout>(R.id.search_button)
        val mediaLibraryButton = findViewById<LinearLayout>(R.id.media_library_button)
        val settingsButton = findViewById<LinearLayout>(R.id.settings_button)

        // Анонимный класс для первой кнопки
        val searchClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Поиск", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchClickListener)

        // Лямбда-выражение для второй кнопки
        mediaLibraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Медиатека", Toast.LENGTH_SHORT).show()
        }

        // onClick для третьей кнопки
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Настройки", Toast.LENGTH_SHORT).show()
        }
    }
}
