package com.example.playlist_maker_2022.presentation.viewModels.media.favouritesSongs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_2022.domain.db.TracksLocalInteractor
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.launch

class FavouriteSongFragmentViewModel(
    private val trackLocalStoragePresenter: TracksLocalInteractor
) : ViewModel() {

    private var searchList = ArrayList<Track>()
    private val _stateLiveData = MutableLiveData(searchList)
    val stateLiveData = _stateLiveData

    fun getFavouritesTracks() {
        searchList.clear()
        viewModelScope.launch {
            trackLocalStoragePresenter.getFavouritesTracks().collect {
                searchList.addAll(it.reversed())
                _stateLiveData.value = searchList
            }
        }
    }
}