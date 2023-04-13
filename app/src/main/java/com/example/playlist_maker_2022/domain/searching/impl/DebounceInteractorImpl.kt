package com.example.playlist_maker_2022.domain.searching.impl

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.playlist_maker_2022.domain.searching.api.DebounceInteractor
import com.example.playlist_maker_2022.presentation.presenters.searching.CreatorTrackPresenter
import com.example.playlist_maker_2022.presentation.presenters.searching.TrackView

class DebounceInteractorImpl : DebounceInteractor {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchRunnable: Runnable? = null

    private fun searchRunnable(track: String, context: Context): Runnable {
        return Runnable {
            CreatorTrackPresenter.providePresenter(view = context as TrackView, trackId = track)
        }
    }

    override fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun searchDebounce(track: String, context: Context) {
        currentSearchRunnable?.let {
            handler.removeCallbacks(it)
        }
        val newSearchRunnable = searchRunnable(track, context)
        currentSearchRunnable = newSearchRunnable
        handler.postDelayed(newSearchRunnable, SEARCH_DEBOUNCE_DELAY)

    }
}