package com.example.playlist_maker_2022.domain.searching.api

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.models.Track

interface TracksInteractor {
    fun getTrackInfo(trackId: String, consumer: TrackInfoConsumer)

    interface TrackInfoConsumer {
        fun consume(track: Pair<NetworkResult, List<Track>>)
    }
}