package com.example.playlist_maker_2022.presentation.viewModels.refactorPlaylist

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.presentation.viewModels.media.createNewPlaylist.NewPlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RefactorPlaylistViewModel(
    private val playListLocalInteractor: PlaylistsLocalInteractor,
) : NewPlaylistViewModel(playListLocalInteractor) {

    private var playlistId = 0

    fun setPlaylistId(text: Int) {
        playlistId = text
    }

    fun updatePlaylist(playlists: Playlists) {
        if (uriLink != Uri.EMPTY) {
            playlists.playlistImage = uriLink.toString()
        }
        playlists.playlistName = textTitle
        playlists.playlistDescription = textDescription
        viewModelScope.launch(Dispatchers.IO) {
            val result =  Playlists(
                playlistId = playlistId,
                playlistName = textTitle,
                playlistDescription = textDescription,
                playlistImage = playlists.playlistImage,
                playlistTracks = playlists.playlistTracks,
                playlistTracksCount = playlists.playlistTracksCount
            )
            playListLocalInteractor.insertPlaylist(result)
        }

    }

    override fun insertPlaylist() = Unit
}