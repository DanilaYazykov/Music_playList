package com.example.playlist_maker_2022.domain.impl

import android.net.Uri
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalRepository
import com.example.playlist_maker_2022.domain.models.Playlist
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

class PlaylistsLocalInteractorImpl(
    private val playlistsLocalRepository: PlaylistsLocalRepository
) : PlaylistsLocalInteractor {
    override suspend fun getTracksFromPlaylist(playlists: List<String>): Flow<List<Track>> {
        return playlistsLocalRepository.getTracksFromPlaylist(playlists)
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: String) {
        playlist.playlistTracks = playlist.playlistTracks - trackId
        playlist.playlistTracksCount = playlist.playlistTracks.size
        playlistsLocalRepository.updatePlaylist(playlist)
        clearTracksFromPlaylist()
    }

    override suspend fun getAllTracksInPlaylists() : Flow<List<Track>> {
        return playlistsLocalRepository.getAllTracks()
    }

    override suspend fun deleteTrack(trackId: String) {
        playlistsLocalRepository.deleteTrack(trackId)
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistsLocalRepository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist, track: Track) {
        playlist.playlistTracks = playlist.playlistTracks + track.trackId
        playlist.playlistTracksCount = playlist.playlistTracks.size
        playlistsLocalRepository.updatePlaylist(playlist)
        playlistsLocalRepository.insertTracksInPlaylist(track)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsLocalRepository.getPlaylists()
    }

    private suspend fun clearTracksFromPlaylist() {
        playlistsLocalRepository.clearTracksFromPlaylist()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsLocalRepository.deletePlaylist(playlist)
        clearTracksFromPlaylist()
    }

    override suspend fun checkIfTrackAlreadyExists(playlist: Playlist, track: Track): Boolean {
        return playlist.playlistTracks.contains(track.trackId)
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): Flow<Pair<File, Uri>> {
        return playlistsLocalRepository.saveImageToPrivateStorage(uri)
    }

}