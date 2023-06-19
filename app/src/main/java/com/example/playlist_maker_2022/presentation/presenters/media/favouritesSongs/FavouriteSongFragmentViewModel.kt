package com.example.playlist_maker_2022.presentation.presenters.media.favouritesSongs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter

class FavouriteSongFragmentViewModel(
    trackStorage: TrackStorageManagerPresenter,
) : ViewModel() {

    private val sharedPreference = trackStorage
    private var searchList = ArrayList<Track>()
    private val _stateLiveData = MutableLiveData(searchList)
    val getStateLiveData = _stateLiveData

    fun getFavouritesTracks() {
        searchList.clear()
        searchList.addAll(sharedPreference.getFavouritesTracks().reversed())
        _stateLiveData.value = searchList
    }
}