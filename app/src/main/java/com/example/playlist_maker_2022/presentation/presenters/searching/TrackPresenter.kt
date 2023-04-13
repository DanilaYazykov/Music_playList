package com.example.playlist_maker_2022.presentation.presenters.searching

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.models.Track


class TrackPresenter(
    private var view: TrackView?,
    private val trackId: String,
    private val tracksInteractor: TracksInteractor,
) {
    private val trackConsumer: TracksInteractor.TrackInfoConsumer =
        object : TracksInteractor.TrackInfoConsumer {
            override fun consume(track: Pair<NetworkResult, List<Track>>) {
                println("Мы в TrackPresenter и передаем треки обратно во вью: $track")
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
}