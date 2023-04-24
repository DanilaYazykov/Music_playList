package com.example.playlist_maker_2022.domain.searching.api

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.models.Track

interface TracksRepository {
    fun likeTrack(trackId: String)
    fun unlikeTrack(trackId: String)
    suspend fun getTrackForId(trackName: String): Pair<NetworkResult, List<Track>>
}