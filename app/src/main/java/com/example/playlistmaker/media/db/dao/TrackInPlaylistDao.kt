package com.example.playlistmaker.media.db.dao

import androidx.room.Dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.db.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): TrackInPlaylistEntity?

    @Query("DELETE FROM track_in_playlist WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)
}