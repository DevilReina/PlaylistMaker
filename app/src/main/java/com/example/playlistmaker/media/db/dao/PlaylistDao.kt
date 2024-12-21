package com.example.playlistmaker.media.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.playlistmaker.media.data.converters.Converters
import com.example.playlistmaker.media.db.PlaylistEntity

@Dao
@TypeConverters(Converters::class)
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    // Добавление трека в плейлист
    @Query("UPDATE playlists_table SET tracks = :tracks WHERE playlistId = :playlistId")
    suspend fun addTrackToPlaylist(playlistId: Long, tracks: String)
}
