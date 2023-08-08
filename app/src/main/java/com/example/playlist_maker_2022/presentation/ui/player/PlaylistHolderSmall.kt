package com.example.playlist_maker_2022.presentation.ui.player

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Playlists

class PlaylistHolderSmall(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private val nameOfPlaylist: TextView? = itemView.findViewById(R.id.tv_name_of_song_small)
    private val countOfTracks: TextView? = itemView.findViewById(R.id.tv_name_of_group_small)
    private val imagePlaylist: ImageView = itemView.findViewById(R.id.iv_album_cover_small)

    fun bind(playlist: Playlists) {
        nameOfPlaylist?.text = playlist.playlistName
        countOfTracks?.text = mapper(playlist.playlistTracksCount)

        Glide.with(itemView)
            .load(playlist.playlistImage)
            .placeholder(R.drawable.no_reply)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(imagePlaylist)
    }

    private fun mapper(countOfTracks: Int): String {
        val text = when {
            countOfTracks % 10 == 1 && countOfTracks % 100 != 11 ->
                "$countOfTracks " + context.getString(R.string.oneTrack)
            countOfTracks % 10 in 2..4 && countOfTracks % 100 !in 12..14
            -> "$countOfTracks " + context.getString(R.string.TwoThreeFourTrack)
            else -> "$countOfTracks " + context.getString(R.string.ManyTracks)
        }
        return text
    }
}

