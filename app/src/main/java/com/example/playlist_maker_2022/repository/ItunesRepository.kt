package com.example.playlist_maker_2022.repository

import com.example.playlist_maker_2022.searchingActivity.ItunesApi
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