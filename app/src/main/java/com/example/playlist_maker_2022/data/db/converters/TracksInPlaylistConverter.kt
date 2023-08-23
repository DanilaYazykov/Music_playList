package com.example.playlist_maker_2022.data.db.converters

import com.example.playlist_maker_2022.data.db.TracksInPlaylistEntities
import com.example.playlist_maker_2022.domain.models.Track

class TracksInPlaylistConverter {
    fun map(track: TracksInPlaylistEntities): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl60 = track.artworkUrl60,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isLiked = track.isLiked
        )
    }

    fun map(track: Track): TracksInPlaylistEntities {
        return TracksInPlaylistEntities(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl60,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isLiked
        )
    }

}