package com.example.playlist_maker_2022.presentation.presenters.searching

import android.os.Handler
import android.os.Looper
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.impl.CheckingInternetUseCases
import android.app.Application
import androidx.lifecycle.*
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.SharedPreferencesPresenter

class SearchViewModel(
    private var application: Application,
    private var trackId: String,
    private val tracksInteractor: TracksInteractor
) : AndroidViewModel(application) {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchRunnable: Runnable? = null

    private var searchList = ArrayList<Track>()
    private val sharedPreference = SharedPreferencesPresenter(application.applicationContext)
    private val searchListLiveData: MutableLiveData<List<Track>> = MutableLiveData()
    fun getSearchListLiveData(): LiveData<List<Track>> = searchListLiveData

    private val trackLiveData: MutableLiveData<Pair<NetworkResult, List<Track>>> = MutableLiveData()
    fun getTrackLiveData(): LiveData<Pair<NetworkResult, List<Track>>> = trackLiveData

    private val internetLiveData: MutableLiveData<Boolean> = MutableLiveData()
    fun getInternetLiveData(): LiveData<Boolean> = internetLiveData

    fun clearTracks() {
        sharedPreference.clearTracks()
        searchList.clear()
        searchListLiveData.value = searchList
    }

    fun getSavedTracks() {
        searchList.addAll(sharedPreference.getSavedTracks())
        searchListLiveData.value = searchList
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
        searchListLiveData.value = searchList
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
            trackLiveData.value = track.copy(second = newTrackList)
        } else {
            trackLiveData.value = track
        }
    }

    fun isNetworkAvailable(application: Application) {
        internetLiveData.value = CheckingInternetUseCases().isNetworkAvailable(application)
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