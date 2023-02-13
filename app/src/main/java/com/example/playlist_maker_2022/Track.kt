package com.example.playlist_maker_2022

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String
)

class TrackResponse(
   // val resultCount: String,
    val results: List<Track>
)

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}