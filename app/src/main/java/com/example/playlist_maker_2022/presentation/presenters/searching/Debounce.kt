package com.example.playlist_maker_2022.presentation.presenters.searching

import com.example.playlist_maker_2022.presentation.ui.searching.SearchingActivity

interface Debounce {
    fun provideDebounce(): Boolean
    fun provideSearchDebounce(track: String, view: SearchingActivity)
}