package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksRequest
import com.example.playlistmaker.search.data.dto.TracksResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.model.Track
import java.io.IOException
import com.example.playlistmaker.search.mappers.TrackMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private val trackMapper = TrackMapper()

    override fun searchTracks(expression: String): Flow<List<Track>> {
        return flow {
            // Выполняем запрос
            val response = networkClient.doRequest(TracksRequest(expression))

            if (response.resultCode == 200) {
                // Преобразуем результат
                emit((response as TracksResponse).results.map { trackDto ->
                    trackMapper.mapTrackDtoToDomain(trackDto) // Используем маппер (Маппинг из DTO в доменную модель)
                })
            } else if (response.resultCode == 500) {
                throw IOException("Network Error") // Обработка ошибки сети
            } else {
                emit(emptyList()) // В случае ошибки возвращаем пустой список
            }
        }.catch { exception ->
            throw exception // Пробрасываем ошибку для дальнейшей обработки
        }
    }
}
