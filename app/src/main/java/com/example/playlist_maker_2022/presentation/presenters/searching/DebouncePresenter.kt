package com.example.playlist_maker_2022.presentation.presenters.searching

import com.example.playlist_maker_2022.domain.searching.api.DebounceInteractor
import com.example.playlist_maker_2022.presentation.ui.searching.SearchingActivity

class DebouncePresenter(private val debounceInteractor: DebounceInteractor): Debounce {

   init {
        debounceInteractor.clickDebounce()
    }

    override fun provideDebounce(): Boolean {
        return debounceInteractor.clickDebounce()
    }

    override fun provideSearchDebounce(track: String, view: SearchingActivity) {
        debounceInteractor.searchDebounce(track = track, context = view)
    }
}