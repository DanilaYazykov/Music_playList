package com.example.playlist_maker_2022.di

import com.example.playlist_maker_2022.presentation.viewModels.openedPlaylist.OpenedPlaylistViewModel
import com.example.playlist_maker_2022.presentation.viewModels.refactorPlaylist.RefactorPlaylistViewModel
import com.example.playlist_maker_2022.presentation.viewModels.media.createNewPlaylist.NewPlaylistViewModel
import com.example.playlist_maker_2022.presentation.viewModels.media.favouritesSongs.FavouriteSongFragmentViewModel
import com.example.playlist_maker_2022.presentation.viewModels.media.playLists.PlaylistMediaFragmentViewModel
import com.example.playlist_maker_2022.presentation.viewModels.player.PlayerViewModel
import com.example.playlist_maker_2022.presentation.viewModels.searching.SearchViewModel
import com.example.playlist_maker_2022.presentation.viewModels.settings.SettingsViewModel
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
            trackLocalStoragePresenter = get(),
            playlistsLocalInteractor = get()
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
        PlaylistMediaFragmentViewModel(
            playlistsLocalInteractor = get()
        )
    }
}

val newPlaylistViewModel = module {
    viewModel<NewPlaylistViewModel> {
        NewPlaylistViewModel(
            playlistsLocalInteractor = get()
        )
    }
}

val openedPlaylistViewModel = module {
    viewModel<OpenedPlaylistViewModel> {
        OpenedPlaylistViewModel(
            playListLocalInteractor = get()
        )
    }
}

val refactorPlaylistViewModel = module {
    viewModel<RefactorPlaylistViewModel> {
        RefactorPlaylistViewModel(
            playListLocalInteractor = get()
        )
    }
}