package com.example.playlist_maker_2022.presentation.viewModels.media.createNewPlaylist

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

open class NewPlaylistViewModel(private val playlistsLocalInteractor: PlaylistsLocalInteractor) :
    ViewModel() {

    protected var textTitle = ""
    protected var textDescription = ""
    protected var uriLink: Uri = Uri.EMPTY

    private val _stateLiveData = MutableLiveData<Pair<File, Uri>>()
    val stateLiveData = _stateLiveData

    open fun insertPlaylist() {
        viewModelScope.launch {
            val playlist = createPlaylist()
            playlistsLocalInteractor.insertPlaylist(playlist)
        }
    }

    fun saveImageToPrivateStorage(uri: Uri) {
        viewModelScope.launch {
            playlistsLocalInteractor.saveImageToPrivateStorage(uri).collect() {
                uriLink = it.second
                _stateLiveData.value = it
            }
        }
    }

    fun setPlaylistName(text: String) {
        textTitle = text
    }

    fun setPlaylistDescription(text: String) {
        textDescription = text
    }

    internal open fun createPlaylist() : Playlist {
        return Playlist(
            playlistId = 0,
            playlistName = textTitle,
            playlistDescription = textDescription,
            playlistImage = if (uriLink != Uri.EMPTY) uriLink.toString() else "",
        )
    }

}