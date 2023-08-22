package com.example.playlist_maker_2022.domain.models

import com.example.playlist_maker_2022.presentation.viewModels.player.PlayStatus

data class PlayerState(
    val playStatus: PlayStatus,
    val currentTime: Int,
    val liked: Boolean
) {
    companion object {
        val Default = PlayerState(PlayStatus.Default, 0, false)
    }
}