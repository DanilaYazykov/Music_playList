package com.example.playlist_maker_2022.data.sharedPreferences

import android.content.SharedPreferences
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.TrackStorageManager
import com.google.gson.Gson

class TrackStorageManagerImpl(
    private val sharedPrefs: SharedPreferences
) : TrackStorageManager {

    override fun getSavedTracks(): ArrayList<Track> {
        val savedBeforeTracks = sharedPrefs.getString(SEARCH_TRACKS_PREFS, null)
        val savedTracks = Gson().fromJson(savedBeforeTracks, Array<Track>::class.java)
        return if (savedTracks != null) {
            ArrayList(savedTracks.toList())
        } else {
            ArrayList()
        }
    }

    override fun saveTracks(tracks: ArrayList<Track>) {
        val tracksJson = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(SEARCH_TRACKS_PREFS, tracksJson)
            .apply()
    }

    override fun clearTracks() {
        sharedPrefs.edit()
            .clear()
            .apply()
    }

    companion object {
        private const val SEARCH_TRACKS_PREFS = "searchTracks"
    }
}