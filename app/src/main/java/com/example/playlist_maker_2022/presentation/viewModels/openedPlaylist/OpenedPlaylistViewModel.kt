package com.example.playlist_maker_2022.presentation.viewModels.openedPlaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OpenedPlaylistViewModel(
    private val playListLocalInteractor: PlaylistsLocalInteractor,
) : ViewModel() {

    private val tracks = mutableListOf<Track>()

    private val uiStateMutable = MutableStateFlow<UpdatedScreenState>(UpdatedScreenState.Default)
    val uiState: StateFlow<UpdatedScreenState> = uiStateMutable.asStateFlow()

    fun getTracksFromPlaylist(playlists: Playlists) {
        viewModelScope.launch {
            playListLocalInteractor.getTracksFromPlaylist(playlists.playlistTracks).collect {
                if (playlists.playlistTracksCount == 0) {
                    uiStateMutable.emit(UpdatedScreenState.EmptyBottomSheet(playlists))
                } else {
                    tracks.clear()
                    tracks.addAll(it)
                    uiStateMutable.emit(UpdatedScreenState.ExpandedBottomSheet(it))
                }
            }
        }
    }

    fun deleteTrack(trackId: String, playlists: Playlists) {
        viewModelScope.launch(Dispatchers.IO) {
            playListLocalInteractor.removeTrackFromPlaylist(trackId = trackId, playlist = playlists)
            getTracksFromPlaylist(playlists)
        }
    }

    fun deletePlaylist(playlists: Playlists) {
        viewModelScope.launch(Dispatchers.IO) {
            playListLocalInteractor.deletePlaylist(playlists)
        }
    }

    fun onSharePressed(playlists: Playlists) {
        viewModelScope.launch(Dispatchers.IO) {
                if (playlists.playlistTracksCount == 0) {
                    uiStateMutable.emit(UpdatedScreenState.EmptyShare)
                } else {
                    uiStateMutable.emit(UpdatedScreenState.SharedPlaylist(playlists, tracks))
            }
        }
    }


    fun onDotsPressed(playlists: Playlists) {
        viewModelScope.launch(Dispatchers.IO) {
            uiStateMutable.emit(UpdatedScreenState.Default)
            playListLocalInteractor.getTracksFromPlaylist(playlists.playlistTracks).collect {
                uiStateMutable.emit(UpdatedScreenState.OptionsMenu(playlists))
            }
        }
    }
}