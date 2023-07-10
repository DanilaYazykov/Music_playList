package com.example.playlist_maker_2022.domain.searching.api

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    suspend fun getTrackForId(trackName: String): Flow<Pair<NetworkResult, List<Track>>>
}