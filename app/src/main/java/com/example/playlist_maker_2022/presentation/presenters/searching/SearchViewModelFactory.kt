package com.example.playlist_maker_2022.presentation.presenters.searching

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker_2022.data.searching.TracksRepositoryImpl
import com.example.playlist_maker_2022.data.network.NetworkClientImpl
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import com.example.playlist_maker_2022.domain.searching.impl.TracksInteractorImpl

class SearchViewModelFactory(
    val track: String,
    val application: Application
) : ViewModelProvider.Factory {

    private fun getRepository(): TracksRepository {
        return TracksRepositoryImpl(NetworkClientImpl())
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(
            application = application,
            trackId = track,
            tracksInteractor = TracksInteractorImpl(getRepository()),
        ) as T
    }
}