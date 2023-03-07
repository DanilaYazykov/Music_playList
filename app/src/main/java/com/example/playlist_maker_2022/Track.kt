package com.example.playlist_maker_2022

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class Track(
    @SerializedName("trackId") val trackId: String,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: String,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("collectionName") val collectionName: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("country") val country: String
)

class TrackResponse(
    val results: List<Track>
)

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}