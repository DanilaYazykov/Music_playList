package com.example.playlist_maker_2022.presentation.presenters.searching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playlist_maker_2022.data.searching.TracksRepositoryImpl
import com.example.playlist_maker_2022.data.network.NetworkClientImpl
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.presentation.util.checkingInternetUtil.CheckingInternetUseCases
import com.example.playlist_maker_2022.domain.searching.impl.TracksInteractorImpl
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.TrackStorageManagerPresenter

class SearchViewModelFactory(
    val track: String
) : ViewModelProvider.Factory {

    private fun getRepository(): TracksRepository {
        return TracksRepositoryImpl(NetworkClientImpl())
    }

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[APPLICATION_KEY])
        val trackStorage = TrackStorageManagerPresenter(application)
        val internet = CheckingInternetUseCases().isNetworkAvailable(application)

        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(
            internet = internet,
            trackStorage = trackStorage,
            trackId = track,
            tracksInteractor = TracksInteractorImpl(getRepository()),
        ) as T
    }
}