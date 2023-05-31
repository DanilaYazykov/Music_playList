package com.example.playlist_maker_2022.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlist_maker_2022.data.ItunesApi
import com.example.playlist_maker_2022.data.NetworkClient
import com.example.playlist_maker_2022.data.network.NetworkClientImpl
import com.example.playlist_maker_2022.data.player.PlayerBasicImpl
import com.example.playlist_maker_2022.data.searching.TracksRepositoryImpl
import com.example.playlist_maker_2022.domain.player.api.PlayerBasic
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    factory { Gson() }

    single<SharedPreferences>(named("theme")) {
        androidContext().getSharedPreferences("theme", Context.MODE_PRIVATE)
    }
    single<SharedPreferences>(named("tracks")) {
        androidContext().getSharedPreferences("tracks", Context.MODE_PRIVATE)
    }
    single<SharedPreferences>(named("favourites")) {
        androidContext().getSharedPreferences("favourites", Context.MODE_PRIVATE)
    }

    single<NetworkClient> { NetworkClientImpl(itunesService = get()) }
    single<TracksRepository> { TracksRepositoryImpl(networkClient = get()) }

    factory { MediaPlayer() }
    factory<PlayerBasic> {
        PlayerBasicImpl(url = get(), mediaPlayer = get())
    }
}