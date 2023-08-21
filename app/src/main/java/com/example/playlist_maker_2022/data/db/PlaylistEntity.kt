package com.example.playlist_maker_2022.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "playlistTable")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistImage: String?,
    var playlistTracks: String,
    var playlistTracksCount: Int = 0
)