package com.example.playlist_maker_2022.domain.db

import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsLocalRepository {

    suspend fun insertPlaylist(playlist: Playlists)

    suspend fun updatePlaylist(playlist: Playlists)

    suspend fun getPlaylists(): Flow<List<Playlists>>

    suspend fun deletePlaylist(playlist: Playlists)
}