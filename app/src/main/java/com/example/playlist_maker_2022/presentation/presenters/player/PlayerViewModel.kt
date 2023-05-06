package com.example.playlist_maker_2022.presentation.presenters.player

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.SharedPreferencesPresenter

class PlayerViewModel(
    application: Application,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var playerState = STATE_DEFAULT
    private val playStatusLiveData = MutableLiveData<PlayStatus>(PlayStatus.Default)
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    private val currentTimeLiveData = MutableLiveData<Int>()
    fun getCurrentTimeLiveData(): LiveData<Int> = currentTimeLiveData
    private val likedLiveData = MutableLiveData<Boolean>()
    fun getLikedLiveData(): LiveData<Boolean> = likedLiveData

    private val sharedPreference = SharedPreferencesPresenter(application.applicationContext)
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                currentTimeLiveData.value = playerInteractor.getCurrentPosition()
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
            playStatusLiveData.value = PlayStatus.Prepared
            playerState = STATE_PREPARED
        }
        playerInteractor.setOnCompletionListener {
            playStatusLiveData.value = PlayStatus.Paused
            playerState = STATE_PREPARED
            currentTimeLiveData.value = 0
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        handler.post(updateTimeRunnable)
        playStatusLiveData.value = PlayStatus.Playing
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playStatusLiveData.value = PlayStatus.Paused
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
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
        likedLiveData.value = hasFavouritesInTrack
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
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DelayMillis = 1000L
    }
}

sealed class PlayStatus {
    object Default : PlayStatus()
    object Prepared : PlayStatus()
    object Playing : PlayStatus()
    object Paused : PlayStatus()
}