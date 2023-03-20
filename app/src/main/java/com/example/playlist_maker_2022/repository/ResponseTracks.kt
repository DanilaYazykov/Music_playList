package com.example.playlist_maker_2022.repository

import android.annotation.SuppressLint
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.searchingActivity.*
import retrofit2.Response

class ResponseTracks {

    fun responseTracks(
        searchText: String,
        itunesService: ItunesApi?,
        trackList: ArrayList<Track>,
        trackAdapter: TrackAdapter,
        binding: ActivitySearchingBinding
    ) {
        if (searchText.isNotEmpty()) {
            itunesService?.search(searchText)?.enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    trackList.clear()
                    response.body()?.results?.let { trackList.addAll(it) }
                    trackAdapter.notifyDataSetChanged()
                    binding.rlProgressBar.visibility = View.GONE
                    SetVisibility(binding).setVisibility(response, trackList, trackAdapter)
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    SetVisibility(binding).setVisibility(null , null, null)
                    binding.btUpdate.setOnClickListener {
                        responseTracks(
                            searchText,
                            itunesService,
                            trackList,
                            trackAdapter,
                            binding
                        )
                    }
                }
            })
        }
    }
}