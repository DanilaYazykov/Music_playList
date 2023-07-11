package com.example.playlist_maker_2022.domain.searching.impl

import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.TrackStorageManager

class TrackStorageManagerInteractor(private val sharedPreferencesManager: TrackStorageManager) {

    fun getSavedTracks(): ArrayList<Track> {
        return sharedPreferencesManager.getSavedTracks()
    }

    fun saveTracks(tracks: ArrayList<Track>) {
        sharedPreferencesManager.saveTracks(tracks)
    }

    fun clearTracks() {
        sharedPreferencesManager.clearTracks()
    }
}