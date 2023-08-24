package com.example.playlist_maker_2022.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlist_maker_2022.data.db.dao.PlaylistDao
import com.example.playlist_maker_2022.data.db.dao.TrackDao

@Database(
    version = 22,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        TracksInPlaylistEntities::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao

    abstract fun getPlaylistDao(): PlaylistDao
}