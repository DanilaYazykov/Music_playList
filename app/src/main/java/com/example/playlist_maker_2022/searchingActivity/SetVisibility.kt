package com.example.playlist_maker_2022.searchingActivity

import android.annotation.SuppressLint
import android.view.View
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import retrofit2.Response

class SetVisibility(private val bdnFun: ActivitySearchingBinding) {

    @SuppressLint("NotifyDataSetChanged")
    fun setVisibility(
        response: Response<TrackResponse>?,
        trackList: ArrayList<Track>?,
        trackAdapter: TrackAdapter?
    ) {
        if (response != null && response.code() == 200) {
            if (trackList != null && trackList.isNotEmpty()) {
                showViews(bdnFun.rcViewSearching)
                response.body()?.results?.let { trackList.addAll(it) }
                trackAdapter?.notifyDataSetChanged()
            } else {
                showViews(bdnFun.iwNoResultLayout)
            }
        } else if (response != null && response.code() == 400) {
            showViews(bdnFun.iwNoConnectionLayout)
        } else {
            showViews(bdnFun.rlProgressBar)
        }
    }

    private fun showViews(vararg views: View) {
        bdnFun.rcViewSearching.visibility = View.GONE
        bdnFun.clSearchHistory.visibility = View.GONE
        bdnFun.iwNoResultLayout.visibility = View.GONE
        bdnFun.iwNoConnectionLayout.visibility = View.GONE
        bdnFun.rlProgressBar.visibility = View.GONE
        for (view in views) {
            view.visibility = View.VISIBLE
        }
    }

    fun buttonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun simpleVisibility(s: Int?) {
        when (s) {
            1 -> showViews(bdnFun.rlProgressBar)
            2 -> showViews(bdnFun.iwNoConnectionLayout)
            3 -> showViews(bdnFun.rcViewSearching)
            else -> Unit
        }
    }
}