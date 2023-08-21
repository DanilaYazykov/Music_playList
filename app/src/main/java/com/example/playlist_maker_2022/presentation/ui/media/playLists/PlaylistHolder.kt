package com.example.playlist_maker_2022.presentation.ui.media.playLists

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

class PlaylistHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private val nameOfPlaylist: TextView?  = itemView.findViewById(R.id.title)
    private val countOfTrack: TextView? = itemView.findViewById(R.id.tracksCount)
    private val image: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(playlist: Playlists) {
        nameOfPlaylist?.text = playlist.playlistName
        countOfTrack?.text = mapper(playlist.playlistTracksCount)

        Glide.with(itemView)
            .load(playlist.playlistImage)
            .placeholder(R.drawable.no_reply)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(image)
    }

    private fun mapper(countOfTracks: Int): String {
        return context.resources.getQuantityString(R.plurals.track_count, countOfTracks, countOfTracks)
    }

}