package com.example.playlist_maker_2022.data.db

import com.example.playlist_maker_2022.data.db.converters.TrackDbConverter
import com.example.playlist_maker_2022.domain.db.TracksLocalRepository
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksLocalRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : TracksLocalRepository {

    override fun getFavouritesTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.getTrackDao().getFavouritesTracks()
        emit(convertFromTracksEntityToTracks(tracks))
    }

    override suspend fun insertTrack(track: Track) {
        val listOf = listOf(trackDbConverter.map(track))
        appDatabase.getTrackDao().insertTrack(listOf[0])
    }

    override suspend fun deleteTrack(track: Track) {
        val listOf = listOf(trackDbConverter.map(track))
        appDatabase.getTrackDao().deleteTrack(listOf[0])
    }

    override suspend fun deleteAllTracks() {
        appDatabase.getTrackDao().deleteAllTracks()
    }

    override suspend fun getTrackById(trackId: String): Flow<Track> = flow {
        val track = appDatabase.getTrackDao().getTrackById(trackId)
        emit(trackDbConverter.map(track))
    }
    private fun convertFromTracksEntityToTracks(tracksEntity: List<TrackEntity>): List<Track> {
        return tracksEntity.map { trackEntity -> trackDbConverter.map(trackEntity) }
    }
}