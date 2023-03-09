package com.example.playlist_maker_2022.searchingActivity

import android.annotation.SuppressLint
import android.view.View
import com.example.playlist_maker_2022.Track
import com.example.playlist_maker_2022.TrackAdapter
import com.example.playlist_maker_2022.TrackResponse
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import retrofit2.Response

class SetVisibility(bdfunEntry: ActivitySearchingBinding){

    private val bdfun = bdfunEntry

    @SuppressLint("NotifyDataSetChanged")
    fun setVisibility(
        response: Response<TrackResponse>?,
        trackList: ArrayList<Track>?,
        trackAdapter: TrackAdapter?
    ) {
        if (response != null && response.code() == 200) {
            if (trackList != null && trackList.isNotEmpty()) {
                response.body()?.results?.let { trackList.addAll(it) }
                trackAdapter?.notifyDataSetChanged()
            } else {
                bdfun.rcViewSearching.visibility = View.GONE
                bdfun.iwNoConnectionLayout.visibility = View.GONE
                bdfun.iwNoResultLayout.visibility = View.VISIBLE
            }
        } else {
            bdfun.rcViewSearching.visibility = View.GONE
            bdfun.iwNoResultLayout.visibility = View.GONE
            bdfun.iwNoConnectionLayout.visibility = View.VISIBLE
        }
    }

    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}

