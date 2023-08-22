package com.example.playlist_maker_2022.presentation.viewModels.media.playLists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.models.Playlists
import kotlinx.coroutines.launch

class PlaylistMediaFragmentViewModel(
    private val playlistsLocalInteractor: PlaylistsLocalInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<List<Playlists>>()
    val stateLiveData = _stateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsLocalInteractor.getPlaylists().collect {
                _stateLiveData.value = it
            }
        }
    }
}