package com.example.playlist_maker_2022.data.searching

import com.example.playlist_maker_2022.data.NetworkClient
import com.example.playlist_maker_2022.data.dto.RequestGetTrack
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override suspend fun getTrackForId(trackName: String): Flow<Pair<NetworkResult, List<Track>>> = flow {
        if (trackName.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            emit(networkClient.doRequest(RequestGetTrack(trackId = trackName)) as Pair<NetworkResult, List<Track>>)
        } else {
            emit(Pair(NetworkResult.NULL_REQUEST, emptyList()))
        }
    }
}