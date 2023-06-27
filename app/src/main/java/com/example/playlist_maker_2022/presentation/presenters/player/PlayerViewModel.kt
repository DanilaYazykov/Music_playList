package com.example.playlist_maker_2022.presentation.presenters.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.models.PlayerState
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    trackStorage: TrackStorageManagerPresenter,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var playerState: PlayStatus = PlayStatus.Default
    private val _state = MutableLiveData(PlayerState.Default)
    val getPlayerStateLiveData: LiveData<PlayerState> = _state

    private val sharedPreference = trackStorage
    private var updateTimeJob: Job? = null

    init {
        preparePlayer()
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
                    _state.value = _state.value?.copy(currentTime = playerInteractor.getCurrentPosition())
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

    fun likeControl(track: String) {
        val favourites = sharedPreference.getFavouritesTracks()
        val hasFavouritesInTrack = favourites.any { favTrack -> track == favTrack.trackId }
        _state.value = _state.value?.copy(liked = hasFavouritesInTrack)
    }

    fun likeTrack(track: Track) {
        sharedPreference.likeTrack(track)
        likeControl(track.trackId)
    }

    fun unlikeTrack(track: Track) {
        sharedPreference.unlikeTrack(track)
        likeControl(track.trackId)
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