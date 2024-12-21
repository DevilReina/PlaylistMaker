package com.example.playlistmaker.media.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.media.data.converters.Converters
import com.example.playlistmaker.media.db.dao.PlaylistDao
import com.example.playlistmaker.media.db.dao.TrackDao


@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}
