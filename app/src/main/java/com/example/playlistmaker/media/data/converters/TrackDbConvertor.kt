package com.example.playlistmaker.media.data.converters

import androidx.room.TypeConverter
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
            trackId = track.trackId,
            trackTimestamp = track.trackTimestamp
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
            trackId = track.trackId,
            trackTimestamp = track.trackTimestamp
        )
    }
    private val gson = Gson()

    @TypeConverter
    fun fromTracksList(tracks: List<Track>): String {
        return gson.toJson(tracks)
    }

    @TypeConverter
    fun toTracksList(data: String): List<Track> {
        val listType = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(data, listType)
    }
}

