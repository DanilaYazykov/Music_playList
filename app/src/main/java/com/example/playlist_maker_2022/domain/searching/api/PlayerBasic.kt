package com.example.playlist_maker_2022.domain.searching.api

import android.media.MediaPlayer

interface PlayerBasic {
    fun preparePlayer()
    fun startPlayer()
    fun pausePlayer()
    fun destroy()
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
}