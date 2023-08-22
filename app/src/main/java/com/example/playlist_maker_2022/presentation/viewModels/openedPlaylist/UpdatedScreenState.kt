package com.example.playlist_maker_2022.presentation.viewModels.openedPlaylist

import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track

sealed interface UpdatedScreenState {
    object Default : UpdatedScreenState
    data class EmptyBottomSheet(val playlists: Playlists) : UpdatedScreenState
    data class ExpandedBottomSheet(val tracks: List<Track>) : UpdatedScreenState
    object EmptyShare : UpdatedScreenState
    data class SharedPlaylist(val playlists: Playlists, val tracks: List<Track>) :
        UpdatedScreenState
    data class OptionsMenu(val playlists: Playlists) : UpdatedScreenState
}