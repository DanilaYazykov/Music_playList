package com.example.playlist_maker_2022.presentation.viewModels.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.db.TracksLocalInteractor
import com.example.playlist_maker_2022.domain.models.PlayerState
import com.example.playlist_maker_2022.domain.models.Playlist
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val trackLocalStoragePresenter: TracksLocalInteractor,
    private val playerInteractor: PlayerInteractor,
    private val playlistsLocalInteractor: PlaylistsLocalInteractor
) : ViewModel() {

    private var playerState: PlayStatus = PlayStatus.Default
    private val _state = MutableLiveData(PlayerState.Default)
    val playerStateLiveData: LiveData<PlayerState> = _state
    private var updateTimeJob: Job? = null
    private val _stateLiveData = MutableLiveData<List<Playlist>>()
    val stateLiveData = _stateLiveData

    private val _insertOrNotFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val insertOrNotFlow = _insertOrNotFlow

    init {
        preparePlayer()
        getPlaylists()
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsLocalInteractor.getPlaylists().collect {
                _stateLiveData.value = it
            }
        }
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer()
        playerInteractor.setOnPreparedListener {
            _state.value = _state.value?.copy(playStatus = PlayStatus.Prepared)
            playerState = PlayStatus.Prepared
        }
        playerInteractor.setOnCompletionListener {
            _state.value = _state.value?.copy(playStatus = PlayStatus.Paused)
            playerState = PlayStatus.Prepared
            _state.value = _state.value?.copy(playStatus = PlayStatus.Paused, currentTime = 0)
            stopUpdatingTime()
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        startUpdatingTime()
        _state.value = _state.value?.copy(playStatus = PlayStatus.Playing)
        playerState = PlayStatus.Playing
    }

    private fun startUpdatingTime() {
        updateTimeJob = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                if (_state.value?.playStatus == PlayStatus.Playing) {
                    _state.value =
                        _state.value?.copy(currentTime = playerInteractor.getCurrentPosition())
                }
                delay(DelayMillis)
            }
        }
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        _state.value = _state.value?.copy(playStatus = PlayStatus.Paused)
        playerState = PlayStatus.Paused
        stopUpdatingTime()
    }

    private fun stopUpdatingTime() {
        updateTimeJob?.cancel()
    }

    fun playbackControl() {
        when (playerState) {
            PlayStatus.Playing -> pausePlayer()
            PlayStatus.Prepared, PlayStatus.Paused -> startPlayer()
            else -> Unit
        }
    }

    public override fun onCleared() {
        super.onCleared()
        playerInteractor.destroy()
        stopUpdatingTime()
    }

    suspend fun likeControl(track: String) {
        trackLocalStoragePresenter.getFavouritesTracks().collect {
            val hasFavouritesInTrack = it.any { favTrack -> track == favTrack.trackId }
            _state.value = _state.value?.copy(liked = hasFavouritesInTrack)
        }
    }

    suspend fun likeTrack(track: Track) {
        trackLocalStoragePresenter.insertTrack(track)
        likeControl(track.trackId)
    }

    suspend fun unlikeTrack(track: Track) {
        trackLocalStoragePresenter.deleteTrack(track)
        likeControl(track.trackId)
    }

    private fun updatePlaylist(name: Playlist, track: Track) {
        viewModelScope.launch {
            playlistsLocalInteractor.updatePlaylist(name, track)
        }
    }

    fun checkPlaylistsAndInsert(playlistTracks: Playlist, track: Track) {
        viewModelScope.launch {
            if (playlistsLocalInteractor.checkIfTrackAlreadyExists(playlistTracks, track)) {
                _insertOrNotFlow.value = true
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    updatePlaylist(playlistTracks, track)
                    _insertOrNotFlow.value = false
                }
            }
        }
    }

    companion object {
        private const val DelayMillis = 300L
    }
}

sealed class PlayStatus {
    object Default : PlayStatus()
    object Prepared : PlayStatus()
    object Playing : PlayStatus()
    object Paused : PlayStatus()
}