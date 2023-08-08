package com.example.playlist_maker_2022.presentation.ui.media.playLists

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Playlists

class PlaylistAdapter(var playlists: List<Playlists>, val context: Context) : RecyclerView.Adapter<PlaylistHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist, parent, false)
        return PlaylistHolder(view, context)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(playlists[position])
    }
}