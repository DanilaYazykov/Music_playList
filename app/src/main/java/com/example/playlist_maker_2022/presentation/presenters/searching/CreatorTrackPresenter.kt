package com.example.playlist_maker_2022.presentation.presenters.searching

import android.content.Context
import com.example.playlist_maker_2022.data.network.NetworkClientImpl
import com.example.playlist_maker_2022.data.TracksRepositoryImpl
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.domain.searching.impl.TracksInteractorImpl
import com.example.playlist_maker_2022.presentation.ui.searching.SearchingActivity

object CreatorTrackPresenter {
    private val trackPresenter = TrackPresenter(null, "", TracksInteractorImpl(getRepository()))
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
    fun provideDebounce(): Boolean {
        return trackPresenter.debounceClick()
    }

    fun provideSearchDebounce(track: String, view: SearchingActivity) {
        trackPresenter.debounceSearch(track, view)
    }
}
