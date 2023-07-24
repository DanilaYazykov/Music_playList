package com.example.playlist_maker_2022.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackTable")
data class TrackEntity(
    @PrimaryKey
    var trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    val isLiked: Boolean
)

