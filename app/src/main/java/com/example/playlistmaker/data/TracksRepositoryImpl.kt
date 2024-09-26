package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksRequest
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.io.IOException
import com.example.playlistmaker.data.mappers.TrackMapper

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private val trackMapper = TrackMapper()

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksRequest(expression))

        return if (response.resultCode == 200) {
            (response as TracksResponse).results.map { trackDto ->
                trackMapper.mapTrackDtoToDomain(trackDto) // Используем маппер (Маппинг из DTO в доменную модель)
            }
        } else if (response.resultCode == 500) {
            // Ошибка сети
            throw IOException("Network Error")
        } else {
            emptyList() // В случае ошибки возвращаем пустой список
        }
    }
}
