package com.example.playlist_maker_2022.domain.impl

import android.net.Uri
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalRepository
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

class PlaylistsLocalInteractorImpl(
    private val playlistsLocalRepository: PlaylistsLocalRepository
) : PlaylistsLocalInteractor {
    override suspend fun insertPlaylist(playlist: Playlists) {
        playlistsLocalRepository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlists, track: Track) {
        playlist.playlistTracks = playlist.playlistTracks + track.trackId
        playlist.playlistTracksCount = playlist.playlistTracks.size
        playlistsLocalRepository.updatePlaylist(playlist)
        playlistsLocalRepository.insertTracksInPlaylist(track)
    }

    override suspend fun getPlaylists(): Flow<List<Playlists>> {
        return playlistsLocalRepository.getPlaylists()
    }

    override suspend fun deletePlaylist(playlist: Playlists) {
        playlistsLocalRepository.deletePlaylist(playlist)
    }

    override suspend fun checkIfTrackAlreadyExists(playlist: Playlists, track: Track): Boolean {
        return playlist.playlistTracks.contains(track.trackId)
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): Flow<Pair<File, Uri>> {
        return playlistsLocalRepository.saveImageToPrivateStorage(uri)
    }

}