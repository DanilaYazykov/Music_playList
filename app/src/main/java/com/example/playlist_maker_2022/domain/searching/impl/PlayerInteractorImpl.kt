package com.example.playlist_maker_2022.domain.searching.impl

import com.example.playlist_maker_2022.domain.searching.api.PlayerBasic
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor

class PlayerInteractorImpl(private val playerBasic: PlayerBasic): PlayerInteractor {

    override fun startPlayer() {
        playerBasic.startPlayer()
    }

    override fun pausePlayer() {
        playerBasic.pausePlayer()
    }

    override fun destroy() {
        playerBasic.destroy()
    }
}