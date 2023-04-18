package com.example.playlist_maker_2022.presentation.presenters.searching

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.impl.CheckingInternetUseCases


class TrackPresenter(
    private var view: TrackView?,
    private val trackId: String,
    private val tracksInteractor: TracksInteractor,
) {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchRunnable: Runnable? = null
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

    private fun searchRunnable(track: String, context: Context): Runnable {
        return Runnable {
            CreatorTrackPresenter.providePresenter(view = context as TrackView, trackId = track)
        }
    }

    fun debounceClick(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun debounceSearch(track: String, context: Context) {
        currentSearchRunnable?.let {
            handler.removeCallbacks(it)
        }
        val newSearchRunnable = searchRunnable(track, context)
        currentSearchRunnable = newSearchRunnable
        handler.postDelayed(newSearchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}