package com.example.playlist_maker_2022.domain.db

import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsLocalInteractor {

    suspend fun insertPlaylist(playlist: Playlists)

    suspend fun updatePlaylist(playlist: Playlists, track: Track)

    suspend fun getPlaylists(): Flow<List<Playlists>>

    suspend fun deletePlaylist(playlist: Playlists)

    suspend fun checkIfTrackAlreadyExists(playlist: Playlists, track: Track): Boolean

}