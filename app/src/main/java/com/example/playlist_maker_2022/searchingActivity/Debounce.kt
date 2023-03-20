package com.example.playlist_maker_2022.searchingActivity

import android.os.Handler
import android.os.Looper
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.repository.ResponseTracks

class Debounce(
    private val itunesService: ItunesApi?,
    private val trackList: ArrayList<Track>,
    private val trackAdapter: TrackAdapter,
    private val binding: ActivitySearchingBinding
) {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    private var isClickAllowed = true
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { ResponseTracks().responseTracks(
            binding.inputEditText.text.toString(),
            itunesService,
            trackList,
            trackAdapter,
            binding
        ) }

    fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_PROGRESSBAR)
    }


    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}


