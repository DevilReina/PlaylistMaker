package com.example.playlistmaker.media.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.db.dao.TrackDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}