package com.example.playlist_maker_2022.data

import com.example.playlist_maker_2022.data.dto.RequestGetTrack
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun likeTrack(trackId: String) = Unit
    override fun unlikeTrack(trackId: String) = Unit

    override suspend fun getTrackForId(trackName: String): Pair<NetworkResult, List<Track>> {
        if (trackName.isNotEmpty()) {
            return networkClient.doRequest(RequestGetTrack(trackId = trackName)) as Pair<NetworkResult, List<Track>>
        }
        return Pair(NetworkResult.NULL_REQUEST, emptyList())
    }
}