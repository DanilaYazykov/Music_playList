package com.example.playlist_maker_2022.domain.searching.api

interface CheckingInternet {
    fun isNetworkAvailable(): Boolean
}