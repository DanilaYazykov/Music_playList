package com.example.playlist_maker_2022.repository

import android.app.appsearch.SearchResult
import com.example.playlist_maker_2022.ItunesApi
import com.example.playlist_maker_2022.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItunesRepository {

    companion object {
        const val itunesUrl = "https://itunes.apple.com"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val itunesService: ItunesApi? = retrofit.create(ItunesApi::class.java)

}