package com.example.playlist_maker_2022.data.db

import com.example.playlist_maker_2022.data.db.converters.PlaylistsDbConverter
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalRepository
import com.example.playlist_maker_2022.domain.models.Playlists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsLocalRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsDbConverter: PlaylistsDbConverter
) : PlaylistsLocalRepository {
    override suspend fun insertPlaylist(playlist: Playlists) {
        val listOf = listOf(playlistsDbConverter.map(playlist))
        appDatabase.getPlaylistDao().insertPlaylist(listOf[0])
    }

    override suspend fun updatePlaylist(playlist: Playlists) {
        appDatabase.getPlaylistDao().updatePlaylist(playlist = playlistsDbConverter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlists>> = flow {
        val playlists = appDatabase.getPlaylistDao().getPlaylists()
        emit(convertFromPlaylistsEntityToPlaylists(playlists))
    }

    override suspend fun deletePlaylist(playlist: Playlists) {
        val listOf = listOf(playlistsDbConverter.map(playlist))
        appDatabase.getPlaylistDao().deletePlaylist(listOf[0])
    }

    private fun convertFromPlaylistsEntityToPlaylists(playlistsEntity: List<PlaylistEntity>): List<Playlists> {
        return playlistsEntity.map { playlistEntity -> playlistsDbConverter.map(playlistEntity) }
    }

}