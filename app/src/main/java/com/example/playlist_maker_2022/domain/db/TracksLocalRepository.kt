package com.example.playlist_maker_2022.domain.db

import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksLocalRepository {

    fun getFavouritesTracks(): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    suspend fun deleteAllTracks()

    suspend fun getTrackById(trackId: String): Flow<Track>

}