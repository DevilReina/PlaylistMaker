package com.example.playlistmaker.domain.impl

import android.util.Log
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.logger.Logger
import java.io.IOException

class TracksInteractorImpl(private val repository: TracksRepository, private val logger: Logger) : TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer, onError: (Throwable) -> Unit) {
        val thread = Thread {
            try {
                // Выполняем поиск треков
                logger.log("TracksInteractor", "Starting search for: $expression")

                val foundTracks = repository.searchTracks(expression)

                logger.log("TracksInteractor", "Search completed. Found ${foundTracks.size} tracks")
                consumer.consume(foundTracks)
            } catch (e: IOException) {
                // Обрабатываем ошибку сети (отсутствие подключения к интернету)
                logger.log("TracksInteractor", "Network error: ${e.message}")
                onError(e) // Передаем ошибку наверх
            } catch (e: Exception) {
                logger.log("TracksInteractor", "Error during search: ${e.message}")
                consumer.consume(emptyList()) // Передаем пустой список в случае других ошибок
            }
        }
        thread.start()
    }
}
