package com.example.playlist_maker_2022.data.network

import android.util.Log
import com.example.playlist_maker_2022.data.ItunesApi
import com.example.playlist_maker_2022.data.NetworkClient
import com.example.playlist_maker_2022.data.TrackResponse
import com.example.playlist_maker_2022.data.dto.RequestGetTrack
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClientImpl : NetworkClient {

    override suspend fun doRequest(dto: Any): Pair<NetworkResult, List<Track>> =
        withContext(Dispatchers.IO) {
            val trackDto = dto as RequestGetTrack
            val trackId = trackDto.trackId

            val retrofit = Retrofit.Builder()
                .baseUrl(itunesUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val itunesService: ItunesApi = retrofit.create(ItunesApi::class.java)
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
    companion object {
        const val itunesUrl = "https://itunes.apple.com"
    }
}