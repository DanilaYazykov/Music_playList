package com.example.playlist_maker_2022.data.network

import com.example.playlist_maker_2022.data.ItunesApi2
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

    companion object {
        const val itunesUrl = "https://itunes.apple.com"
    }

    override suspend fun doRequest(dto: Any): Pair<NetworkResult, List<Track>> =
        withContext(Dispatchers.IO) {
            val trackDto = dto as RequestGetTrack
            val trackId = trackDto.trackId

            val retrofit = Retrofit.Builder()
                .baseUrl(itunesUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val itunesService: ItunesApi2 = retrofit.create(ItunesApi2::class.java)
            try {
                val request: Response<TrackResponse> = itunesService.search(trackId)
                println("NetworkClientImpl, Request: ${request.raw().request().url()}")
                println("NetworkClientImpl, Response code: ${request.code()}")
                println("NetworkClientImpl, Response body: ${request.body()}")
                println("NetworkClientImpl, Response error body: ${request.errorBody()}")
                when {
                    request.code() == 200 && request.body() != null && request.body()!!.results.isNotEmpty() -> {
                        val trackResponse = request.body()!!
                        val trackList = trackResponse.results
                        println("МЫ в NetworkClientImpl и получили ответ от сервера track: $trackList !")
                        Pair(NetworkResult.SUCCESS, trackList)
                    }
                    request.code() == 200 && request.body() != null && request.body()!!.results.isEmpty() -> {
                        println("МЫ в NetworkClientImpl и получили ответ от сервера(треки отсутствуют)")
                        Pair(NetworkResult.TRACKS_NOT_FOUND, emptyList())
                    }
                    else -> {
                        println("МЫ в NetworkClientImpl и получили ответ от сервера(ошибка): ${request.errorBody()} !")
                        Pair(NetworkResult.ERROR, emptyList())
                    }
                }
            } catch (e: Exception) {
                println("МЫ в NetworkClientImpl и произошла ошибка: ${e.message}")
                Pair(NetworkResult.ERROR, emptyList())
            }
        }
}