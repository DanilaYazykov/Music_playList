package com.example.playlist_maker_2022.presentation.ui.searching

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Track

class TrackAdapter(internal var trackList: List<Track>, private val listener: OnTrackClickListener) :
    RecyclerView.Adapter<TrackHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return TrackHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val track = trackList[position]
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            listener.onTrackClick(track)
        }
    }

    override fun getItemCount(): Int = trackList.size
}

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}