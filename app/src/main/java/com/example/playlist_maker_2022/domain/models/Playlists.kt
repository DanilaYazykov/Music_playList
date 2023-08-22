package com.example.playlist_maker_2022.domain.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Playlists(
    val playlistId: Int,
    var playlistName: String,
    var playlistDescription: String?,
    var playlistImage: String?,
    var playlistTracks: List<String> = listOf(),
    var playlistTracksCount: Int = 0) : Parcelable
