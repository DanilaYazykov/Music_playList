package com.example.playlist_maker_2022.domain.searching.api

import com.example.playlist_maker_2022.domain.models.Track

interface TrackStorageManager {

    fun getSavedTracks(): ArrayList<Track>
    fun saveTracks(tracks: ArrayList<Track>)
    fun clearTracks()
    fun likeTrack(track: Track)
    fun unlikeTrack(track: Track)
    fun getFavouritesTracks(): ArrayList<Track>
}