package com.example.playlist_maker_2022.presentation.presenters.searching

import android.content.Context
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.DebounceInteractor
import com.example.playlist_maker_2022.domain.searching.impl.CheckingInternetUseCases
import com.example.playlist_maker_2022.presentation.ui.searching.SearchingActivity


class TrackPresenter(
    private var view: TrackView?,
    private val trackId: String,
    private val tracksInteractor: TracksInteractor,
) {
    private val trackConsumer: TracksInteractor.TrackInfoConsumer =
        object : TracksInteractor.TrackInfoConsumer {
            override fun consume(track: Pair<NetworkResult, List<Track>>) {
                view?.drawTrack(track)
            }
        }

    init {
        tracksInteractor.getTrackInfo(trackId, trackConsumer)
    }

    fun likeTrack() {
        tracksInteractor.likeTrack(trackId, trackConsumer)
    }

    fun unlikeTrack() {
        tracksInteractor.unlikeTrack(trackId, trackConsumer)
    }

    fun onViewDestroyed() {
        view = null
    }

    fun isNetworkAvailable(context: Context): Boolean {
        return CheckingInternetUseCases().isNetworkAvailable(context)
    }

    fun provideDebounce(debounceInteractor: DebounceInteractor): Boolean {
            return debounceInteractor.clickDebounce()
        }

    fun provideSearchDebounce(debounceInteractor: DebounceInteractor, track: String, view: SearchingActivity) {
            debounceInteractor.searchDebounce(track = track, context = view)
        }
    }