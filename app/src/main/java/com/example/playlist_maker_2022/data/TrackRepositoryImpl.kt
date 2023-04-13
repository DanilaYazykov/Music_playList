package com.example.playlist_maker_2022.data

import com.example.playlist_maker_2022.data.dto.RequestGetTrack
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    //  private val cachedTracks = mutableMapOf<String, List<Track>>()
    override fun likeTrack(trackId: String) {
/*        val response = networkClient.doRequest(RequestGetTrack(trackId = trackId)) as Track

        if (response.resultCode == 1) {
            cachedTracks[trackId]?.let { track: Track ->
                cachedTracks[trackId] = track.copy(isLiked = false)
            }
        }*/
    }

    override fun unlikeTrack(trackId: String) {
/*        val response = networkClient.doRequest(RequestGetTrack(trackId = trackId)) as Track

        if (response.resultCode == 1) {
            cachedTracks[trackId]?.let { track: Track ->
                cachedTracks[trackId] = track.copy(isLiked = true)
            }
        }*/
    }

    override suspend fun getTrackForId(trackName: String): Pair<NetworkResult, List<Track>> {
        println("МЫ в TracksRepositoryImpl и делаем запрос: $trackName!")
        if (trackName.isNotEmpty()) {
            val response =
                networkClient.doRequest(RequestGetTrack(trackId = trackName)) as Pair<NetworkResult, List<Track>>
            println("МЫ в TracksRepositoryImpl и передаем трек во вью: $response!")
            // cachedTracks[trackName] = response.second
            return response
        }
        return Pair(NetworkResult.NULL_REQUEST, emptyList())
    }
}