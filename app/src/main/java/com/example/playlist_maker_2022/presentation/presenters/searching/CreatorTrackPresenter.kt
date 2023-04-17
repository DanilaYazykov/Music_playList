package com.example.playlist_maker_2022.presentation.presenters.searching

import android.content.Context
import com.example.playlist_maker_2022.data.network.NetworkClientImpl
import com.example.playlist_maker_2022.data.TracksRepositoryImpl
import com.example.playlist_maker_2022.domain.searching.api.DebounceInteractor
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.domain.searching.impl.TracksInteractorImpl
import com.example.playlist_maker_2022.presentation.ui.searching.SearchingActivity

object CreatorTrackPresenter {
    private fun getRepository(): TracksRepository {
        return TracksRepositoryImpl(NetworkClientImpl())
    }

    fun providePresenter(view: TrackView, trackId: String): TrackPresenter {
        return TrackPresenter(
            view = view,
            trackId = trackId,
            tracksInteractor = TracksInteractorImpl(getRepository()),
        )
    }

    fun checkingInternetPresenter(context: Context): Boolean {
       return TrackPresenter(null,"null",TracksInteractorImpl(getRepository())).isNetworkAvailable(context)
    }

    fun provideDebounce(debounceInteractor: DebounceInteractor): Boolean {
        return TrackPresenter(null,"null",TracksInteractorImpl(getRepository())).provideDebounce(
            debounceInteractor
        )
    }

    fun provideSearchDebounce(debounceInteractor: DebounceInteractor, track: String, view: SearchingActivity) {
        TrackPresenter(null,"null",TracksInteractorImpl(getRepository())).provideSearchDebounce(
            debounceInteractor = debounceInteractor,
            track = track,
            view = view
        )
    }
}
