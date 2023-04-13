package com.example.playlist_maker_2022.domain.searching.api

import android.content.Context

interface CheckingInternet {
    fun isNetworkAvailable(context: Context?): Boolean
}