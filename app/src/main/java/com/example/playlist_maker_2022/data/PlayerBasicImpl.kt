package com.example.playlist_maker_2022.data

import android.media.MediaPlayer
import com.example.playlist_maker_2022.domain.searching.api.PlayerBasic

class PlayerBasicImpl(private val url: String?): PlayerBasic {

    var mediaPlayer = MediaPlayer()
    private var isPrepared = false

    fun preparePlayer() {
        isPrepared = true
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        println("PlayerBasicImpl.preparePlayer мы в preparePlayer.")
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
}