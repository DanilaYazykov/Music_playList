package com.example.playlist_maker_2022.domain.searching.api

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.models.Track

interface TracksRepository {

    suspend fun getTrackForId(trackName: String): Pair<NetworkResult, List<Track>>
}