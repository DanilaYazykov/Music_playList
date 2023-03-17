package com.example.playlist_maker_2022.searchingActivity

import android.os.Handler
import android.os.Looper
import android.widget.EditText
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.repository.ResponseTracks

class Debounce(
    private val editText: EditText,
    private val itunesService: ItunesApi?,
    private val trackList: ArrayList<Track>,
    private val trackAdapter: TrackAdapter,
    private val bdnFun: ActivitySearchingBinding
) {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { ResponseTracks().responseTracks(
            editText.text.toString(),
            itunesService,
            trackList,
            trackAdapter,
            bdnFun
        ) }

    fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        SetVisibility(bdnFun).simpleVisibility(1)
    }

    private var isClickAllowed = true

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}


