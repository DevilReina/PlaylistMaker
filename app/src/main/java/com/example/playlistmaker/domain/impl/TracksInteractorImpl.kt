package com.example.playlistmaker.domain.impl

import android.util.Log
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.io.IOException

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer, onError: (Throwable) -> Unit) {
        val thread = Thread {
            try {
                // Выполняем поиск треков
                Log.d("TracksInteractor", "Starting search for: $expression")
                val foundTracks = repository.searchTracks(expression)

                Log.d("TracksInteractor", "Search completed. Found ${foundTracks.size} tracks")
                consumer.consume(foundTracks)
            } catch (e: IOException) {
                // Обрабатываем ошибку сети (отсутствие подключения к интернету)
                Log.e("TracksInteractor", "Network error: ${e.message}")
                onError(e) // Передаем ошибку наверх
            } catch (e: Exception) {
                Log.e("TracksInteractor", "Error during search: ${e.message}", e)
                consumer.consume(emptyList()) // Передаем пустой список в случае других ошибок
            }
        }
        thread.start()
    }
}
