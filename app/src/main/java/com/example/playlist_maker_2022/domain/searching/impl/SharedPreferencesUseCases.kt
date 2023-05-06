package com.example.playlist_maker_2022.domain.searching.impl

import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.SharedPreferencesManager

class SharedPreferencesUseCases(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun getSavedTracks(): ArrayList<Track> {
        return sharedPreferencesManager.getSavedTracks()
    }

    fun saveTracks(tracks: ArrayList<Track>) {
        sharedPreferencesManager.saveTracks(tracks)
    }

    fun clearTracks() {
        sharedPreferencesManager.clearTracks()
    }

    fun likeTrack(tracks: Track) {
        sharedPreferencesManager.likeTrack(tracks)
    }

    fun unlikeTrack(tracks: Track) {
        sharedPreferencesManager.unlikeTrack(tracks)
    }

    fun getFavouritesTracks(): ArrayList<Track> {
        return sharedPreferencesManager.getFavouritesTracks()
    }
}