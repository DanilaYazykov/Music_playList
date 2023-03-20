package com.example.playlist_maker_2022.searchingActivity

import android.annotation.SuppressLint
import android.view.View
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import retrofit2.Response

class SetVisibility(private val binding: ActivitySearchingBinding) {

    companion object {
        const val SHOW_PROGRESSBAR = 1
        const val SHOW_NO_CONNECTION = 2
        const val SHOW_SEARCHING_RESULT = 3
        const val SHOW_HISTORY_SEARCHING_RESULT = 4
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setVisibility(
        response: Response<TrackResponse>?,
        trackList: ArrayList<Track>?,
        trackAdapter: TrackAdapter?
    ) {
        if (response != null && response.code() == 200) {
            if (trackList != null && trackList.isNotEmpty()) {
                showViews(binding.rcViewSearching)
                response.body()?.results?.let { trackList.addAll(it) }
                trackAdapter?.notifyDataSetChanged()
            } else {
                showViews(binding.iwNoResultLayout)
            }
        } else {
            showViews(binding.iwNoConnectionLayout)
        }
    }

    private fun showViews(vararg views: View) {
        binding.rcViewSearching.visibility = View.GONE
        binding.clSearchHistory.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.GONE
        binding.rlProgressBar.visibility = View.GONE
        for (view in views) {
            view.visibility = View.VISIBLE
        }
    }

    fun buttonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun simpleVisibility(s: Int?) {
        when (s) {
            SHOW_PROGRESSBAR -> showViews(binding.rlProgressBar)
            SHOW_NO_CONNECTION -> showViews(binding.iwNoConnectionLayout)
            SHOW_SEARCHING_RESULT -> showViews(binding.rcViewSearching)
            SHOW_HISTORY_SEARCHING_RESULT -> showViews(binding.clSearchHistory)
            else -> Unit
        }
    }
}