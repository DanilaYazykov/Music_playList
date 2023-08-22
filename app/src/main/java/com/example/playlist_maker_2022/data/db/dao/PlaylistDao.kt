package com.example.playlist_maker_2022.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlist_maker_2022.data.db.PlaylistEntity
import com.example.playlist_maker_2022.data.db.TracksInPlaylistEntities

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(entity = TracksInPlaylistEntities::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTracksInPlaylist(tracksInPlaylistEntities: TracksInPlaylistEntities)

    @Query("SELECT * FROM tracksInPlaylistTable WHERE trackId IN (:trackId)")
    suspend fun getTracksInPlaylist(trackId: String): List<TracksInPlaylistEntities>

    @Query("SELECT * FROM playlistTable")
    suspend fun getAllPlaylists() : List<PlaylistEntity>

    @Query("SELECT * FROM tracksInPlaylistTable")
    suspend fun getAllTracks() : List<TracksInPlaylistEntities>

    @Query("DELETE FROM tracksInPlaylistTable WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlistTable")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlist: PlaylistEntity)

}