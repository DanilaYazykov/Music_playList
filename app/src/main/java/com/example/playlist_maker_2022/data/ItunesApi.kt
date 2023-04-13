package com.example.playlist_maker_2022.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/*interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}*/

interface ItunesApi2 {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): Response<TrackResponse>
}
