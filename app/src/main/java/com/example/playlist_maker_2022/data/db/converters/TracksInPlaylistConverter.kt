package com.example.playlist_maker_2022.data.db.converters

import com.example.playlist_maker_2022.data.db.TracksInPlaylistEntities
import com.example.playlist_maker_2022.domain.models.Track

class TracksInPlaylistConverter {
    fun map(track: TracksInPlaylistEntities): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isLiked
        )
    }

    fun map (track: Track): TracksInPlaylistEntities {
        return TracksInPlaylistEntities(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
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