package com.example.playlistmaker.search.mappers

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.model.Track

class TrackMapper {
    fun mapTrackDtoToDomain(trackDto: TrackDto): Track {
        return Track(
            trackName = trackDto.trackName ?: "Unknown Track",
            artistName = trackDto.artistName ?: "Unknown Artist",
            trackTimeMillis = trackDto.trackTimeMillis ?: 0L,
            artworkUrl100 = trackDto.artworkUrl100 ?: "",
            collectionName = trackDto.collectionName ?: "Unknown Collection",
            releaseDate = trackDto.releaseDate ?: "Unknown Date",
            primaryGenreName = trackDto.primaryGenreName ?: "Unknown Genre",
            country = trackDto.country ?: "Unknown Country",
            previewUrl = trackDto.previewUrl,
            trackId = trackDto.trackId
        )
    }
}