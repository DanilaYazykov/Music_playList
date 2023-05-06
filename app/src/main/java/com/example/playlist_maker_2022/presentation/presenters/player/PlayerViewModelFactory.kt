package com.example.playlist_maker_2022.presentation.presenters.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker_2022.data.player.PlayerBasicImpl
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.player.impl.PlayerInteractorImpl

class PlayerViewModelFactory(
    val track: Track?,
    val application: Application
) : ViewModelProvider.Factory {

    private val playerInteractor = PlayerInteractorImpl(
        PlayerBasicImpl(track?.previewUrl)
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PlayerViewModel(
            application = application,
            playerInteractor = playerInteractor
        ) as T
    }
}