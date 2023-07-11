package com.example.playlist_maker_2022.presentation.presenters.localStorage

import android.content.Context
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.impl.TrackStorageManagerInteractor

class TrackStorageManagerPresenter(val context: Context, private val shared: TrackStorageManagerInteractor) {

    fun getSavedTracks(): ArrayList<Track> {
        return shared.getSavedTracks()
    }

    fun saveTracks(tracks: ArrayList<Track>) {
        shared.saveTracks(tracks)
    }

    fun clearTracks() {
        shared.clearTracks()
    }
}