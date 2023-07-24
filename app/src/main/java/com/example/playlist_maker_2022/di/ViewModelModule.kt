package com.example.playlist_maker_2022.di

import com.example.playlist_maker_2022.presentation.presenters.media.favouritesSongs.FavouriteSongFragmentViewModel
import com.example.playlist_maker_2022.presentation.presenters.media.playLists.PlaylistMediaFragmentViewModel
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
            trackLocalStoragePresenter = get()
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
            playerInteractor = get(),
            trackLocalStoragePresenter = get()
        )
    }
}

val favouriteSongFragmentViewModel = module {
    viewModel<FavouriteSongFragmentViewModel> {
        FavouriteSongFragmentViewModel(
            trackLocalStoragePresenter = get()
        )
    }
}


val playlistMediaFragmentViewModel = module {
    viewModel<PlaylistMediaFragmentViewModel> {
        PlaylistMediaFragmentViewModel()
    }
}