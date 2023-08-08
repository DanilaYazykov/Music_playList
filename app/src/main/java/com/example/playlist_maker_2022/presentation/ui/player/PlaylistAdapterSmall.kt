package com.example.playlist_maker_2022.presentation.ui.player

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Playlists

class PlaylistAdapterSmall(
    var playlists: List<Playlists>,
    val context: Context,
    private val listener: OnPlayListClickListener
    ): RecyclerView.Adapter<PlaylistHolderSmall>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolderSmall {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_small, parent, false)
        return PlaylistHolderSmall(view, context)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistHolderSmall, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            listener.onPlaylistClick(playlists[position])
        }
    }
}
interface OnPlayListClickListener {
    fun onPlaylistClick(playlists: Playlists)
}
