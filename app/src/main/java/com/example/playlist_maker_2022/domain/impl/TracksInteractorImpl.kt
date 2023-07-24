package com.example.playlist_maker_2022.domain.impl

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override suspend fun getTrackInfo(trackId: String): Flow<Pair<NetworkResult, List<Track>>> {
        return repository.getTrackForId(trackId)
    }
}