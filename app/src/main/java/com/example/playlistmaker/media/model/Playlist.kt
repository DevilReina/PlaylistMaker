package com.example.playlistmaker.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long? = null,
    val title: String,
    val description: String?,
    val imageUri: String?,
    val tracks: String?,
    val numberOfTracks: Long
) : Parcelable