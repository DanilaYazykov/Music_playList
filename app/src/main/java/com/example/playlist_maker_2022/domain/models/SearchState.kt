package com.example.playlist_maker_2022.domain.models

import com.example.playlist_maker_2022.data.network.NetworkResult

data class SearchState(
    val searchList: List<Track>,
    val trackList: Pair<NetworkResult, List<Track>>,
    val internet: Boolean
) {
    companion object {
        fun default() = SearchState(
            searchList = emptyList(),
            trackList = Pair(NetworkResult.SUCCESS, emptyList()),
            internet = true
        )
    }
}