package com.example.playlist_maker_2022.domain.db

import android.net.Uri
import com.example.playlist_maker_2022.domain.models.Playlist
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PlaylistsLocalInteractor {

    suspend fun getTracksFromPlaylist(playlists: List<String>) : Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: String)

    suspend fun getAllTracksInPlaylists() : Flow<List<Track>>

    suspend fun deleteTrack(trackId: String)

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist, track: Track)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun checkIfTrackAlreadyExists(playlist: Playlist, track: Track): Boolean

    suspend fun saveImageToPrivateStorage(uri: Uri) : Flow<Pair<File, Uri>>

}