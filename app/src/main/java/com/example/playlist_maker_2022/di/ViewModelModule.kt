package com.example.playlist_maker_2022.di

import com.example.playlist_maker_2022.presentation.presenters.player.PlayerViewModel
import com.example.playlist_maker_2022.presentation.presenters.searching.SearchViewModel
import com.example.playlist_maker_2022.presentation.presenters.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchingViewModel = module {
    viewModel<SearchViewModel> { (trackId: String) ->
        SearchViewModel(
            internet = get(),
            trackStorage = get(),
            trackId = trackId,
            tracksInteractor = get(),
        )
    }
}

val settingsViewModel = module {
    viewModel<SettingsViewModel> {
        SettingsViewModel(
            themeStatus = get()
        )
    }
}

val playerViewModel = module {
    viewModel<PlayerViewModel> {
        PlayerViewModel(
            trackStorage = get(),
            playerInteractor = get()
        )
    }
}