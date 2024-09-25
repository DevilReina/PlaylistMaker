package com.example.playlistmaker.data.dto

data class TracksResponse(
    val resultCount: Int,
    val results: ArrayList<TrackDto>
) : Response()