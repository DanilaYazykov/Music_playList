package com.example.playlist_maker_2022.presentation.presenters.media.createNewPlaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.models.Playlists
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistsLocalInteractor: PlaylistsLocalInteractor) :
    ViewModel() {

    fun insertPlaylist(name: Playlists) {
        viewModelScope.launch {
            playlistsLocalInteractor.insertPlaylist(name)
        }
    }

}