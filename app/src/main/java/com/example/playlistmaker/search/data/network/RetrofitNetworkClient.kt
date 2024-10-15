package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksRequest
import java.io.IOException

class RetrofitNetworkClient(private val apiService: ApiService) : NetworkClient
{


    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TracksRequest) {
                val resp = apiService.searchTracks(dto.expression).execute()

                val body = resp.body() ?: Response()
                body.apply { resultCode = resp.code() }
            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: IOException) {
            // Ошибка сети или отсутствие подключения
            Response().apply { resultCode = 500 }
        }
    }
}