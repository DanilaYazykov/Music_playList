package com.example.playlist_maker_2022.domain.db

import android.net.Uri
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PlaylistsLocalInteractor {

    suspend fun getTracksFromPlaylist(playlists: List<String>) : Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(playlist: Playlists, trackId: String)

    suspend fun getAllTracksInPlaylists() : Flow<List<Track>>

    suspend fun deleteTrack(trackId: String)

    suspend fun insertPlaylist(playlist: Playlists)

    suspend fun updatePlaylist(playlist: Playlists, track: Track)

    suspend fun getPlaylists(): Flow<List<Playlists>>

    suspend fun deletePlaylist(playlist: Playlists)

    suspend fun checkIfTrackAlreadyExists(playlist: Playlists, track: Track): Boolean

    suspend fun saveImageToPrivateStorage(uri: Uri) : Flow<Pair<File, Uri>>

}