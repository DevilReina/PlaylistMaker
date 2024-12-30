package com.example.playlistmaker.media.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistTitle: String,
    val playlistDescription: String?,
    val playlistCoverUri: String?,
    val tracks: String?,
    val numberOfTracks: Long
)