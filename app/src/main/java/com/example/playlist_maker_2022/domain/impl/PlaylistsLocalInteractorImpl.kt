package com.example.playlist_maker_2022.domain.impl

import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalRepository
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsLocalInteractorImpl(
    private val playlistsLocalRepository: PlaylistsLocalRepository
) : PlaylistsLocalInteractor {
    override suspend fun insertPlaylist(playlist: Playlists) {
        playlistsLocalRepository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlists, track: Track) {
        playlist.playlistTracks = playlist.playlistTracks + track
        playlist.playlistTracksCount = playlist.playlistTracks.size
        playlistsLocalRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlists>> {
        return playlistsLocalRepository.getPlaylists()
    }

    override suspend fun deletePlaylist(playlist: Playlists) {
        playlistsLocalRepository.deletePlaylist(playlist)
    }

    override suspend fun checkIfTrackAlreadyExists(playlist: Playlists, track: Track): Boolean {
        return playlist.playlistTracks.contains(track)
    }

}