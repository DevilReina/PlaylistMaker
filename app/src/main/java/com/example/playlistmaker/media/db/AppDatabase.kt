package com.example.playlistmaker.media.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.playlistmaker.media.db.dao.PlaylistDao
import com.example.playlistmaker.media.db.dao.TrackDao
import com.example.playlistmaker.media.db.dao.TrackInPlaylistDao


@Database(version = 5, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])

abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

    companion object {

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE track_in_playlist (
                trackId INTEGER PRIMARY KEY NOT NULL,
                trackTimestamp INTEGER NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT NOT NULL,
                releaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT
            )
            """.trimIndent()
                )
            }
        }


        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                )
                    .addMigrations(MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}