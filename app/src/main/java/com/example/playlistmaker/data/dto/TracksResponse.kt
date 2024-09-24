package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

data class TracksResponse(
    val resultCount: Int,
    val results: ArrayList<Track>
) : Response()