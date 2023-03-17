package com.example.playlist_maker_2022.repository

import android.annotation.SuppressLint
import com.example.playlist_maker_2022.searchingActivity.TrackResponse
import com.example.playlist_maker_2022.searchingActivity.SetVisibility
import retrofit2.Call
import retrofit2.Callback
import com.example.playlist_maker_2022.searchingActivity.ItunesApi
import com.example.playlist_maker_2022.searchingActivity.Track
import com.example.playlist_maker_2022.searchingActivity.TrackAdapter
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import retrofit2.Response

class ResponseTracks {

    fun responseTracks(
        searchText: String,
        itunesService: ItunesApi?,
        trackList: ArrayList<Track>,
        trackAdapter: TrackAdapter,
        bdnFun: ActivitySearchingBinding
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
                    SetVisibility(bdnFun).setVisibility(response, trackList, trackAdapter)
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    SetVisibility(bdnFun).setVisibility(null , null, null)
                    bdnFun.btUpdate.setOnClickListener {
                        responseTracks(
                            searchText,
                            itunesService,
                            trackList,
                            trackAdapter,
                            bdnFun
                        )
                    }
                }
            })
        }
    }
}