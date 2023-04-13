package com.example.playlist_maker_2022.presentation.presenters.searching

import com.example.playlist_maker_2022.data.network.NetworkClientImpl
import com.example.playlist_maker_2022.data.TracksRepositoryImpl
import com.example.playlist_maker_2022.domain.searching.impl.TracksInteractorImpl

object CreatorTrackPresenter {
    private fun getRepository(): TracksRepositoryImpl {
        return TracksRepositoryImpl(NetworkClientImpl())
    }

    fun providePresenter(view: TrackView, trackId: String): TrackPresenter {
        return TrackPresenter(
            view = view,
            trackId = trackId,
            tracksInteractor = TracksInteractorImpl(getRepository()),
        )
    }
}
