package com.example.playlist_maker_2022.presentation.presenters.media.favouritesSongs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker_2022.domain.db.TracksLocalInteractor
import com.example.playlist_maker_2022.domain.models.Track

class FavouriteSongFragmentViewModel(
    private val trackLocalStoragePresenter: TracksLocalInteractor
) : ViewModel() {

    private var searchList = ArrayList<Track>()
    private val _stateLiveData = MutableLiveData(searchList)
    val getStateLiveData = _stateLiveData

    suspend fun getFavouritesTracks() {
        searchList.clear()
        trackLocalStoragePresenter.getFavouritesTracks().collect {
            searchList.addAll(it.reversed())
            _stateLiveData.value = searchList
        }
    }
}