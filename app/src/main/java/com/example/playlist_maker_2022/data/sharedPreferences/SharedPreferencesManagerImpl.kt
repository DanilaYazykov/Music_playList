package com.example.playlist_maker_2022.data.sharedPreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.SharedPreferencesManager
import com.google.gson.Gson

class SharedPreferencesManagerImpl(context: Context) : SharedPreferencesManager {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("tracks", MODE_PRIVATE)

    override fun getSavedTracks(): ArrayList<Track> {
        val savedBeforeTracks = sharedPrefs.getString("searchTracks", null)
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
            .putString("searchTracks", tracksJson)
            .apply()
    }

    override fun clearTracks() {
        sharedPrefs.edit()
            .clear()
            .apply()
    }




    // Другие методы для работы с SharedPreferences
}