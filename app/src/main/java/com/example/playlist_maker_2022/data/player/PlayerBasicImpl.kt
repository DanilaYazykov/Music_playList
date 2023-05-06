package com.example.playlist_maker_2022.data.player

import android.media.MediaPlayer
import com.example.playlist_maker_2022.domain.player.api.PlayerBasic

class PlayerBasicImpl(private val url: String?): PlayerBasic {

    private var mediaPlayer = MediaPlayer()
    private var isPrepared = false

    override fun preparePlayer() {
        isPrepared = true
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }

    override fun startPlayer() {
        if (isPrepared) {
            mediaPlayer.start()
        } else {
            preparePlayer()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
        }
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun destroy() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mediaPlayer.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer.setOnCompletionListener(listener)
    }
}