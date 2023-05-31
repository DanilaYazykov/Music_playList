package com.example.playlist_maker_2022.data.sharedPreferences

import android.content.SharedPreferences
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.api.TrackStorageManager
import com.google.gson.Gson

class TrackStorageManagerImpl(
    private val sharedPrefs: SharedPreferences,
    private val sharedFavourites: SharedPreferences
) : TrackStorageManager {

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

    override fun likeTrack(track: Track) {
        val mutableSet = getFavouritesTracks().toMutableSet()
        mutableSet.add(track)
        sharedFavourites.edit()
            .putString(FAVOURITES_PREFS, Gson().toJson(mutableSet))
            .apply()
    }

    override fun unlikeTrack(track: Track) {
        val mutableSet = getFavouritesTracks().toMutableSet()
        mutableSet.remove(track)
        sharedFavourites.edit()
            .putString(FAVOURITES_PREFS, Gson().toJson(mutableSet))
            .apply()
    }

    override fun getFavouritesTracks(): ArrayList<Track> {
        val savedFavouritesTracks = sharedFavourites.getString(FAVOURITES_PREFS, null)
        val favouritesTracks = Gson().fromJson(savedFavouritesTracks, Array<Track>::class.java)
        return if (favouritesTracks != null) {
            ArrayList(favouritesTracks.toList())
        } else {
            ArrayList()
        }
    }

    companion object {
        private const val FAVOURITES_PREFS = "favourites"
    }
}