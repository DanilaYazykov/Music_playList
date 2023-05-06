package com.example.playlist_maker_2022.domain.player.impl

import android.media.MediaPlayer
import com.example.playlist_maker_2022.domain.player.api.PlayerBasic
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor

class PlayerInteractorImpl(private val playerBasic: PlayerBasic): PlayerInteractor {
    override fun preparePlayer() {
        playerBasic.preparePlayer()
    }

    override fun startPlayer() {
        playerBasic.startPlayer()
    }

    override fun pausePlayer() {
        playerBasic.pausePlayer()
    }

    override fun destroy() {
        playerBasic.destroy()
    }

    override fun getCurrentPosition(): Int {
        return playerBasic.getCurrentPosition()
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        playerBasic.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        playerBasic.setOnCompletionListener(listener)
    }
}