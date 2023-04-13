package com.example.playlist_maker_2022.domain.searching.api

import android.content.Context

interface DebounceInteractor {
    fun clickDebounce(): Boolean
    fun searchDebounce(track: String, context: Context)
}