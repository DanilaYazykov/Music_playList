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

    private val sharedFavourites: SharedPreferences =
        context.getSharedPreferences("favourites", MODE_PRIVATE)

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

    override fun likeTrack(tracks: Track) {
        changeFavorites(tracks = tracks, remove = false)
    }

    override fun unlikeTrack(tracks: Track) {
        changeFavorites(tracks = tracks, remove = true)
    }

    override fun getFavouritesTracks(): ArrayList<Track> {
        val savedFavouritesTracks = sharedFavourites.getString("favourites", null)
        val favouritesTracks = Gson().fromJson(savedFavouritesTracks, Array<Track>::class.java)
        return if (favouritesTracks != null) {
            ArrayList(favouritesTracks.toList())
        } else {
            ArrayList()
        }
    }

    private fun changeFavorites(tracks: Track, remove: Boolean) {
        val mutableSet = getFavouritesTracks().toMutableSet()
        if (remove) {
            mutableSet.remove(tracks)
        } else {
            mutableSet.add(tracks)
        }
        sharedFavourites.edit()
            .putString("favourites", Gson().toJson(mutableSet))
            .apply()
    }
}