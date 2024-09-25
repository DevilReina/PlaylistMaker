package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksRequest
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.io.IOException

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksRequest(expression))

        return if (response.resultCode == 200) {
            (response as TracksResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else if (response.resultCode == 500) {
            // Ошибка сети
            throw IOException("Network Error")
        } else {
            emptyList() // Другие ошибки
        }
    }
}
