package com.example.playlist_maker_2022.domain.models

data class Playlists(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistImage: String?,
    var playlistTracks: List<Track> = listOf(),
    var playlistTracksCount: Int = 0
)
