package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksRequest
import com.example.playlistmaker.search.data.dto.TracksResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.model.Track
import java.io.IOException
import com.example.playlistmaker.search.mappers.TrackMapper

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
