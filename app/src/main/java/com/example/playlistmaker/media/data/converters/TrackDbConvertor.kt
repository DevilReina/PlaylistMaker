package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.model.Track

class TrackDbConvertor {

    fun map(track: TrackEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            trackId = track.trackId
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            trackId = track.trackId
        )
    }

    fun map(track: TrackDto): TrackEntity {
        return TrackEntity(
            trackName = track.trackName ?: "",
            artistName = track.artistName ?: "",
            trackTimeMillis = track.trackTimeMillis ?: 0L,
            artworkUrl100 = track.artworkUrl100 ?: "",
            collectionName = track.collectionName ?: "",
            releaseDate = track.releaseDate ?: "",
            primaryGenreName = track.primaryGenreName ?: "",
            country = track.country ?: "",
            previewUrl = track.previewUrl,
            trackId = track.trackId
        )
    }
}

