package com.example.playlist_maker_2022.presentation.presenters.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playlist_maker_2022.data.player.PlayerBasicImpl
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.player.impl.PlayerInteractorImpl
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter

class PlayerViewModelFactory(
    val track: Track?
) : ViewModelProvider.Factory {

    private val playerInteractor = PlayerInteractorImpl(
        PlayerBasicImpl(track?.previewUrl)
    )

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[APPLICATION_KEY])
        val trackStorage = TrackStorageManagerPresenter(application)
        @Suppress("UNCHECKED_CAST")
        return PlayerViewModel(
            trackStorage = trackStorage,
            playerInteractor = playerInteractor
        ) as T
    }
}