package com.example.playlist_maker_2022.presentation.presenters.sharedPreferences

import android.content.Context
import com.example.playlist_maker_2022.data.sharedPreferences.SharedPreferencesManagerImpl
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.impl.SharedPreferencesUseCases

class SharedPreferencesPresenter(val context: Context) {

    private val shared = SharedPreferencesUseCases(sharedPreferencesManager = SharedPreferencesManagerImpl(context = context))

    fun getSavedTracks(): ArrayList<Track> {
        return shared.getSavedTracks()
    }

    fun saveTracks(tracks: ArrayList<Track>) {
        shared.saveTracks(tracks)
    }

    fun clearTracks() {
        shared.clearTracks()
    }

    fun likeTrack(tracks: Track) {
        shared.likeTrack(tracks)
    }

    fun unlikeTrack(tracks: Track) {
        shared.unlikeTrack(tracks)
    }

    fun getFavouritesTracks(): ArrayList<Track> {
        return shared.getFavouritesTracks()
    }
}