package com.example.playlistmaker.media.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey
    val playlistId: Long,
    val playlistTitle: String,
    val playlistDescription: String,
    val playlistCoverUri: String,
    val tracks: List<Long>,
    val numberOfTracks: Long
)