package com.example.playlist_maker_2022.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlist_maker_2022.data.db.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: TrackEntity)

    @Query("SELECT * FROM trackTable")
    suspend fun getFavouritesTracks(): List<TrackEntity>

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM trackTable")
    suspend fun deleteAllTracks()

    @Query("SELECT trackId FROM trackTable")
    suspend fun getFavouritesTracksId(): List<String>

}