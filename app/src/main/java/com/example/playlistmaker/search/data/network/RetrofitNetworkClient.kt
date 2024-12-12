package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksRequest
import retrofit2.HttpException
import java.io.IOException

class RetrofitNetworkClient(private val apiService: ApiService) : NetworkClient
{


    override suspend fun doRequest(dto: Any): Response {
        return try {
            if (dto is TracksRequest) {
                val tracksResponse = apiService.searchTracks(dto.expression)
                tracksResponse.apply { resultCode = 200 } // Указываем код успеха вручную
            } else {
                Response().apply { resultCode = 400 } // Неверный тип запроса
            }
        } catch (e: HttpException) {
            Response().apply {
                resultCode = e.code() // Указываем код ответа из исключения
            }
        }catch (e: IOException) {
            Response().apply { resultCode = 500 } // Ошибка сети
        }
    }
}