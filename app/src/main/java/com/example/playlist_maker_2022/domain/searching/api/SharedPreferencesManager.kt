package com.example.playlist_maker_2022.domain.searching.api

import com.example.playlist_maker_2022.domain.models.Track

interface SharedPreferencesManager {

    fun getSavedTracks(): ArrayList<Track>
    fun saveTracks(tracks: ArrayList<Track>)
    fun clearTracks()
}