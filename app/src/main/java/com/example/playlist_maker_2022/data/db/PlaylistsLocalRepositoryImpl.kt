package com.example.playlist_maker_2022.data.db

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.playlist_maker_2022.data.db.converters.PlaylistsDbConverter
import com.example.playlist_maker_2022.data.db.converters.TracksInPlaylistConverter
import com.example.playlist_maker_2022.domain.db.PlaylistsLocalRepository
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistsLocalRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsDbConverter: PlaylistsDbConverter,
    private val tracksInPlaylistConverter: TracksInPlaylistConverter,
    private val context: Context
) : PlaylistsLocalRepository {
    override suspend fun getTracksFromPlaylist(playlists: List<String>): Flow<List<Track>> = flow {
        val tracksInPlaylist: List<String> = playlists
        val result: MutableList<Track> = mutableListOf()
        for (trackId in tracksInPlaylist) {
            val trackEntities = appDatabase.getPlaylistDao().getTracksInPlaylist(trackId)
            for (i in trackEntities) {
                result.add(tracksInPlaylistConverter.map(i))
            }
        }
        emit(result.reversed())
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllPlaylists(): Flow<List<Playlists>> = flow {
        val playlists = appDatabase.getPlaylistDao().getAllPlaylists()
        emit(convertFromPlaylistsEntityToPlaylists(playlists))
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.getPlaylistDao().getAllTracks()
        val result = tracks.map { tracksInPlaylistConverter.map(it) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteTrack(trackId: String) {
        appDatabase.getPlaylistDao().deleteTrack(trackId)
    }

    override suspend fun insertPlaylist(playlist: Playlists) {
        appDatabase.getPlaylistDao().insertPlaylist(playlistsDbConverter.map(playlist))
    }

    override suspend fun insertTracksInPlaylist(track: Track) {
        val result = tracksInPlaylistConverter.map(track)
        appDatabase.getPlaylistDao().insertTracksInPlaylist(result)
    }

    override suspend fun updatePlaylist(playlist: Playlists) {
        appDatabase.getPlaylistDao().updatePlaylist(playlist = playlistsDbConverter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlists>> = flow {
        val playlists = appDatabase.getPlaylistDao().getPlaylists()
        emit(convertFromPlaylistsEntityToPlaylists(playlists))
    }

    override suspend fun deletePlaylist(playlist: Playlists) {
        appDatabase.getPlaylistDao().deletePlaylist(playlistsDbConverter.map(playlist))
    }

    override suspend fun clearTracksFromPlaylist() {
        var playlists: List<Playlists> = emptyList()
        var tracks: List<Track> = emptyList()

        getPlaylists().collect {
            playlists = it
        }
        getAllTracks().collect {
            tracks = it
        }

        for (track in tracks) {
            var isTrackInPlaylist = false
            for (playlist in playlists) {
                if (playlist.playlistTracks.contains(track.trackId)) {
                    isTrackInPlaylist = true
                    break
                }
            }
            if (!isTrackInPlaylist) {
                deleteTrack(track.trackId)
            }
        }
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): Flow<Pair<File, Uri>> {
        return flow {
            if (uri == Uri.EMPTY) {
                emit(Pair(File(""), Uri.EMPTY))
                return@flow
            }

            val contextWrapper = ContextWrapper(context.applicationContext)
            val filePath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (filePath != null) {
                if (!filePath.exists()) {
                    filePath.mkdirs()
                }
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val playlistImage = File(filePath, "cover_$timestamp.jpg")
            val uriLink = playlistImage.toUri()

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(playlistImage)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            emit(Pair(playlistImage, uriLink))
        }.flowOn(Dispatchers.IO)
    }

    private fun convertFromPlaylistsEntityToPlaylists(playlistsEntity: List<PlaylistEntity>): List<Playlists> {
        return playlistsEntity.map { playlistEntity -> playlistsDbConverter.map(playlistEntity) }
    }

}