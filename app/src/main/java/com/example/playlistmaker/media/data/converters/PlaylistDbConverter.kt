package com.example.playlistmaker.media.data.converters

import android.util.Log
import com.example.playlistmaker.media.db.PlaylistEntity
import com.example.playlistmaker.media.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        Log.d("START MAP1", "START MAP1 playlist: $playlist")
        return PlaylistEntity(
            0,
            playlist.title,
            playlist.description,
            playlist.imageUri,
            playlist.tracks,
            playlist.numberOfTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        Log.d("START MAP2", "START MAP2 playlist: $playlist")
        return Playlist(
            playlist.playlistId,
            playlist.playlistTitle,
            playlist.playlistDescription,
            playlist.playlistCoverUri,
            playlist.tracks,
            playlist.numberOfTracks
        )
    }

}
