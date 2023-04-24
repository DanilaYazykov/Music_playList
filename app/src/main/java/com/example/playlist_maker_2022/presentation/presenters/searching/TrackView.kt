package com.example.playlist_maker_2022.presentation.presenters.searching

import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.models.Track

interface TrackView {
    fun updateTrackLiked(liked: Boolean)
    fun drawTrack(track: Pair<NetworkResult, List<Track>>)
}