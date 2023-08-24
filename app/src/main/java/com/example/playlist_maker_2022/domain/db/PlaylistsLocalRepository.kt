package com.example.playlist_maker_2022.domain.db

import android.net.Uri
import com.example.playlist_maker_2022.domain.models.Playlist
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PlaylistsLocalRepository {

    suspend fun getTracksFromPlaylist(playlists: List<String>) : Flow<List<Track>>

    suspend fun getAllPlaylists() : Flow<List<Playlist>>

    suspend fun getAllTracks() : Flow<List<Track>>

    suspend fun deleteTrack(trackId: String)

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun insertTracksInPlaylist(track: Track)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun clearTracksFromPlaylist()

    suspend fun saveImageToPrivateStorage(uri: Uri) : Flow<Pair<File, Uri>>
}