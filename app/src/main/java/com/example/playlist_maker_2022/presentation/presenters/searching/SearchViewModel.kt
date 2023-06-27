package com.example.playlist_maker_2022.presentation.presenters.searching

import android.util.Log
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.models.Track
import androidx.lifecycle.*
import com.example.playlist_maker_2022.domain.models.SearchState
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter
import com.example.playlist_maker_2022.presentation.util.checkingInternetUtil.CheckingInternetUtil
import kotlinx.coroutines.*

class SearchViewModel(
    private var internet: CheckingInternetUtil,
    trackStorage: TrackStorageManagerPresenter,
    private var trackId: String,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private var networkCheckJob: Job? = null
    private var isClickAllowed = true
    private var searchJob: Job? = null

    private var searchList = ArrayList<Track>()
    private val sharedPreference = trackStorage
    private val _stateLiveData = MutableLiveData(SearchState.default())
    val getStateLiveData = _stateLiveData

    init {
        getSavedTracks()
    }
    fun clearTracks() {
        sharedPreference.clearTracks()
        searchList.clear()
        _stateLiveData.value = _stateLiveData.value?.copy(searchList = searchList)
    }

    private fun getSavedTracks() {
        searchList.addAll(sharedPreference.getSavedTracks())
        _stateLiveData.value = _stateLiveData.value?.copy(searchList = searchList)
    }

    fun onSearchTrackClicked(track: Track) {
        val existingTrack = searchList.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            searchList.remove(existingTrack)
            searchList.add(0, existingTrack)
        } else {
            searchList.add(0, track)
        }
        if (searchList.size > 10) {
            searchList.removeLast()
        }
        _stateLiveData.value = _stateLiveData.value?.copy(searchList = searchList)
        sharedPreference.saveTracks(searchList)
    }

    private fun loadTrack() {/*
        tracksInteractor.getTrackInfo(trackId, object : TracksInteractor.TrackInfoConsumer {
            override fun consume(track: Pair<NetworkResult, List<Track>>) {
                updateFavouritesTracks(track)
            }
        })*/
        viewModelScope.launch {
            tracksInteractor
                .getTrackInfo(trackId)
                .collect { pair ->
                    Log.e("AAAAA", "loadTrack: $pair")
                    updateFavouritesTracks(pair)
                }
        }

    }

    private fun updateFavouritesTracks(track: Pair<NetworkResult, List<Track>>) {
        val favourites = sharedPreference.getFavouritesTracks()
        val hasFavouritesInTrack =
            favourites.any { favTrack -> track.second.any { it.trackId == favTrack.trackId } }
        if (hasFavouritesInTrack) {
            val newTrackList =
                track.second.sortedWith(compareByDescending { favourites.contains(it) })
            _stateLiveData.value = _stateLiveData.value?.copy(trackList = track.copy(second = newTrackList))
        } else {
            _stateLiveData.value = _stateLiveData.value?.copy(trackList = track)
        }
    }

    private fun checkNetwork() {
        networkCheckJob?.cancel()
        networkCheckJob = CoroutineScope(Dispatchers.Main).launch {
            delay (SEARCH_DEBOUNCE_DELAY)
            val result = internet.isNetworkAvailable()
            _stateLiveData.value = _stateLiveData.value?.copy(internet = result)
        }
    }

    private fun searchRunnable(track: String) {
        checkNetwork()
        trackId = track
        loadTrack()
    }

    fun debounceClick(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun debounceSearch(track: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRunnable(track)
        }
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}