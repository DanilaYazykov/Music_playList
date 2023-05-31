package com.example.playlist_maker_2022.presentation.presenters.searching

import android.os.Handler
import android.os.Looper
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.models.Track
import androidx.lifecycle.*
import com.example.playlist_maker_2022.domain.models.SearchState
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter
import kotlinx.coroutines.*
import java.lang.Runnable

class SearchViewModel(
    private var internet: Boolean,
    trackStorage: TrackStorageManagerPresenter,
    private var trackId: String,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private var networkCheckJob: Job? = null
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchRunnable: Runnable? = null

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

    private fun loadTrack() {
        tracksInteractor.getTrackInfo(trackId, object : TracksInteractor.TrackInfoConsumer {
            override fun consume(track: Pair<NetworkResult, List<Track>>) {
                updateFavouritesTracks(track)
            }
        })
    }

    fun updateFavouritesTracks(track: Pair<NetworkResult, List<Track>>) {
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

    fun checkNetwork() {
        networkCheckJob?.cancel()
        networkCheckJob = CoroutineScope(Dispatchers.Main).launch {
            delay (SEARCH_DEBOUNCE_DELAY)
            _stateLiveData.value = _stateLiveData.value?.copy(internet = internet)
        }
    }

    private fun searchRunnable(track: String): Runnable {
        return Runnable {
            trackId = track
            loadTrack()
        }
    }

    fun debounceClick(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun debounceSearch(track: String) {
        currentSearchRunnable?.let {
            handler.removeCallbacks(it)
        }
        val newSearchRunnable = searchRunnable(track)
        currentSearchRunnable = newSearchRunnable
        handler.postDelayed(newSearchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}