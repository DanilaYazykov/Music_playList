package com.example.playlist_maker_2022.repository

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.example.playlist_maker_2022.TrackResponse
import com.example.playlist_maker_2022.checkings.CheckingInternet
import com.example.playlist_maker_2022.searchingActivity.SetVisibility
import retrofit2.Call
import retrofit2.Callback
import android.provider.Settings
import com.example.playlist_maker_2022.ItunesApi
import com.example.playlist_maker_2022.Track
import com.example.playlist_maker_2022.TrackAdapter
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.searchingActivity.SearchingActivity
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
                    bdnFun.rcViewSearching.visibility = View.VISIBLE
                    trackList.clear()
                    response.body()?.results?.let { trackList.addAll(it) }
                    trackAdapter.notifyDataSetChanged()
                    SetVisibility(bdnFun).setVisibility(response, trackList, trackAdapter)
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    SetVisibility(bdnFun).setVisibility(null, null, null)
                    val checkingConnection = CheckingInternet()
                    if (!checkingConnection.isNetworkAvailable(SearchingActivity())) {
                        CheckingInternet.DialogManager.internetSettingsDialog(
                            context = SearchingActivity(),
                            listener = object : CheckingInternet.DialogManager.Listener {
                                override fun onClick(name: String?) {
                                    startActivity(
                                        SearchingActivity(),
                                        Intent(Settings.ACTION_WIFI_SETTINGS),
                                        null
                                    )
                                }
                            })
                    }
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