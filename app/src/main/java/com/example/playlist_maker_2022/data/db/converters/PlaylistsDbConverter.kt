package com.example.playlist_maker_2022.data.db.converters

import com.example.playlist_maker_2022.data.db.PlaylistEntity
import com.example.playlist_maker_2022.domain.models.Playlists
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class PlaylistsDbConverter {

    fun map(playlist: PlaylistEntity): Playlists {
        return Playlists(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistImage = playlist.playlistImage,
            playlistDescription = playlist.playlistDescription,
            playlistTracks = Json.decodeFromString(playlist.playlistTracks),
            playlistTracksCount = playlist.playlistTracksCount
        )
    }

    fun map(playlist: Playlists): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistImage = playlist.playlistImage,
            playlistTracks = Json.encodeToString(playlist.playlistTracks),
            playlistTracksCount = playlist.playlistTracksCount
        )
    }
}