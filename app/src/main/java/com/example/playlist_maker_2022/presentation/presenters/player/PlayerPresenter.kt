package com.example.playlist_maker_2022.presentation.presenters.player

import android.os.Handler
import com.example.playlist_maker_2022.data.PlayerBasicImpl
import com.example.playlist_maker_2022.domain.searching.impl.PlayerInteractorImpl
import com.example.playlist_maker_2022.presentation.ui.player.PlayerActivity

class PlayerPresenter(
    url: String?,
    private val view: PlayerView,
    private val handler: Handler
) {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    val player = PlayerBasicImpl(url = url)
    private val playerInteractor = PlayerInteractorImpl(player)
    private var playerState = STATE_DEFAULT

    private val searchRunnable = object : Runnable {
        override fun run() {
            view.setCurrentTime()
            handler.postDelayed(this, PlayerActivity.delayMillis)
        }
    }

    fun preparePlayer() {
        player.preparePlayer()
        player.mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        player.mediaPlayer.setOnCompletionListener {
            view.setButtonToPlay()
            view.setStartTime()
            playerState = STATE_PREPARED
            handler.removeCallbacks(searchRunnable)
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        handler.post(searchRunnable)
        view.setButtonToPause()
        playerState = STATE_PLAYING
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        view.setButtonToPlay()
        playerState = STATE_PAUSED
        handler.removeCallbacks(searchRunnable)
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

    fun destroy() {
        playerInteractor.destroy()
        handler.removeCallbacks(searchRunnable)
    }
}