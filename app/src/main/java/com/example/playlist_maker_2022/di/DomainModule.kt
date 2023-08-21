package com.example.playlist_maker_2022.di

import com.example.playlist_maker_2022.data.sharedPreferences.ThemeStatus
import com.example.playlist_maker_2022.data.sharedPreferences.TrackStorageManagerImpl
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalInteractor
import com.example.playlist_maker_2022.domain.db.TracksLocalInteractor
import com.example.playlist_maker_2022.domain.impl.PlaylistsLocalInteractorImpl
import com.example.playlist_maker_2022.domain.player.impl.PlayerInteractorImpl
import com.example.playlist_maker_2022.domain.searching.api.PlayerInteractor
import com.example.playlist_maker_2022.domain.searching.api.TrackStorageManager
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.impl.TrackStorageManagerInteractor
import com.example.playlist_maker_2022.domain.impl.TracksInteractorImpl
import com.example.playlist_maker_2022.domain.impl.TracksLocalInteractorImpl
import com.example.playlist_maker_2022.presentation.presenters.localStorage.TrackStorageManagerPresenter
import com.example.playlist_maker_2022.presentation.util.checkingInternetUtil.CheckingInternetUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {
    factory<TrackStorageManager> {
        TrackStorageManagerImpl(
            sharedPrefs = get(named("tracks"))
        )
    }
    single<TracksInteractor> { TracksInteractorImpl(repository = get()) }

    single<ThemeStatus> { ThemeStatus(sharedPrefs = get(named("theme"))) }

    factory<TrackStorageManagerPresenter> {
        TrackStorageManagerPresenter(context = androidContext(), shared = get())
    }

    factory<CheckingInternetUtil> {
        CheckingInternetUtil(androidContext())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(playerBasic = get())
    }

    factory<TrackStorageManagerInteractor> {
        TrackStorageManagerInteractor(sharedPreferencesManager = get())
    }

    single<TracksLocalInteractor> {
        TracksLocalInteractorImpl(get())
    }

    single<PlaylistsLocalInteractor> {
        PlaylistsLocalInteractorImpl(get())
    }

}