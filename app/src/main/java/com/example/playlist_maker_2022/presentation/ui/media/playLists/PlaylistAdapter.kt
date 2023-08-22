package com.example.playlist_maker_2022.presentation.ui.media.playLists

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Playlists

class PlaylistAdapter(var playlists: List<Playlists>, val context: Context,
                      private val listener: OnPlaylistClickListener) : RecyclerView.Adapter<PlaylistHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.playlist, parent, false)
        return PlaylistHolder(itemView, context)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val playlistsCurrent = playlists[position]
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            listener.onPlaylistClick(playlistsCurrent)
        }
    }
}

interface OnPlaylistClickListener {
    fun onPlaylistClick(playlists: Playlists)
}