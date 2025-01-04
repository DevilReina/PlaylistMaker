package com.example.playlistmaker.media.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entities.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int): Int

    @Query("SELECT * FROM favorite_tracks ORDER BY trackTimestamp DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getIdTracks(): List<Int>
}