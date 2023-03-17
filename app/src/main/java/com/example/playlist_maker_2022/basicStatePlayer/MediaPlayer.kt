/*
package com.example.playlist_maker_2022.basicStatePlayer

import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageButton
import com.example.playlist_maker_2022.R

class MediaPlayer(private val play: ImageButton?, private val mediaPlayer: MediaPlayer) : MediaPlayer() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var url = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"

    internal fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play?.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
//            play.text = "PLAY"
            play?.setImageResource(R.drawable.bt_play_night)
            playerState = STATE_PREPARED
        }
    }

    fun startPlayer() {
        playerState = STATE_PLAYING
        mediaPlayer.start()
    //    play.text = "PAUSE"
        play?.setImageResource(R.drawable.bt_play_day)
        Log.e("TAG", "startPlayer: РАБОТАЕТ. МУЗЫКА ИГРАЕТ")
    }

    internal fun pausePlayer() {
        mediaPlayer.pause()
     //   play.text = "PLAY"
        play?.setImageResource(R.drawable.bt_play_night)
        playerState = STATE_PAUSED
        Log.e("TAG", "pausePlayer: ПАУЗА. МУЗЫКА НЕ ИГРАЕТ")

    }

    internal fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    internal fun releasePlayer() {
        mediaPlayer.release()
    }
}*/
