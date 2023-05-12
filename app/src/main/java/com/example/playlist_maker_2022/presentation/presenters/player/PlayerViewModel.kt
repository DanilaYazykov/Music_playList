package com.example.playlist_maker_2022.presentation.presenters.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker_2022.domain.models.PlayerState
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter

class PlayerViewModel(
    trackStorage: TrackStorageManagerPresenter,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var playerState: PlayStatus = PlayStatus.Default
    private val _state = MutableLiveData(PlayerState.Default)
    val getPlayerStateLiveData: LiveData<PlayerState> = _state

    private val sharedPreference = trackStorage
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            when (_state.value?.playStatus) {
                PlayStatus.Playing -> _state.value = _state.value?.copy(currentTime = playerInteractor.getCurrentPosition())
                else -> Unit
            }
            handler.postDelayed(this, DelayMillis)
        }
    }

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
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        handler.post(updateTimeRunnable)
        _state.value = _state.value?.copy(playStatus = PlayStatus.Playing)
        playerState = PlayStatus.Playing
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        _state.value = _state.value?.copy(playStatus = PlayStatus.Paused)
        playerState = PlayStatus.Paused
        handler.removeCallbacks(updateTimeRunnable)
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
        handler.removeCallbacks(updateTimeRunnable)
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
        private const val DelayMillis = 1000L
    }
}

sealed class PlayStatus {
    object Default : PlayStatus()
    object Prepared : PlayStatus()
    object Playing : PlayStatus()
    object Paused : PlayStatus()
}