package com.example.playlist_maker_2022.data.network

import com.example.playlist_maker_2022.data.ItunesApi
import com.example.playlist_maker_2022.data.NetworkClient
import com.example.playlist_maker_2022.data.TrackResponse
import com.example.playlist_maker_2022.data.dto.RequestGetTrack
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class NetworkClientImpl(private val itunesService: ItunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Pair<NetworkResult, List<Track>> =
        withContext(Dispatchers.IO) {
            val trackDto = dto as RequestGetTrack
            val trackId = trackDto.trackId

            try {
                val request: Response<TrackResponse> = itunesService.search(trackId)
                when {
                    request.code() == 200 && request.body() != null && request.body()!!.results.isNotEmpty() -> {
                        val trackResponse = request.body()!!
                        val trackList = trackResponse.results
                        Pair(NetworkResult.SUCCESS, trackList)
                    }
                    request.code() == 200 && request.body() != null && request.body()!!.results.isEmpty() -> {
                        Pair(NetworkResult.TRACKS_NOT_FOUND, emptyList())
                    }
                    else -> {
                        Pair(NetworkResult.ERROR, emptyList())
                    }
                }
            } catch (e: Exception) {
                Pair(NetworkResult.ERROR, emptyList())
            }
        }
}