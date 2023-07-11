package com.example.playlist_maker_2022.domain.searching.impl

import com.example.playlist_maker_2022.domain.db.TracksLocalInteractor
import com.example.playlist_maker_2022.domain.db.TracksLocalRepository
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksLocalInteractorImpl(private val tracksLocalRepository: TracksLocalRepository) : TracksLocalInteractor {
    override fun getFavouritesTracks(): Flow<List<Track>> {
        return tracksLocalRepository.getFavouritesTracks()
    }

    override suspend fun insertTrack(track: Track) {
        tracksLocalRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        tracksLocalRepository.deleteTrack(track)
    }

    override suspend fun deleteAllTracks() {
        tracksLocalRepository.deleteAllTracks()
    }
}