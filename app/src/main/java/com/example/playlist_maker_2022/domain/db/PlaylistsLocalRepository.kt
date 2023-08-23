package com.example.playlist_maker_2022.domain.db

import android.net.Uri
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PlaylistsLocalRepository {

    suspend fun getTracksFromPlaylist(playlists: List<String>) : Flow<List<Track>>

    suspend fun getAllPlaylists() : Flow<List<Playlists>>

    suspend fun getAllTracks() : Flow<List<Track>>

    suspend fun deleteTrack(trackId: String)

    suspend fun insertPlaylist(playlist: Playlists)

    suspend fun insertTracksInPlaylist(track: Track)

    suspend fun updatePlaylist(playlist: Playlists)

    suspend fun getPlaylists(): Flow<List<Playlists>>

    suspend fun deletePlaylist(playlist: Playlists)

    suspend fun clearTracksFromPlaylist()

    suspend fun saveImageToPrivateStorage(uri: Uri) : Flow<Pair<File, Uri>>
}