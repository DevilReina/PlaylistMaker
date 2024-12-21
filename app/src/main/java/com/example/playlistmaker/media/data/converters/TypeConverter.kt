package com.example.playlistmaker.media.data.converters

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromListToString(tracks: List<Long>): String {
        return tracks.joinToString(separator = ",")
    }

    @TypeConverter
    fun fromStringToList(tracks: String): List<Long> {
        return if (tracks.isEmpty()) {
            emptyList()
        } else {
            tracks.split(",").map { it.toLong() }
        }
    }
}