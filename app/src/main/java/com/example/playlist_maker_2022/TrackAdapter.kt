package com.example.playlist_maker_2022

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val trackList: List<Track>, private val listener: OnTrackClickListener) :
    RecyclerView.Adapter<TrackHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return TrackHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        try {
            val track = trackList [position]

            holder.bind(trackList[position])
            holder.itemView.setOnClickListener {
                listener.onTrackClick(track)
            }

        } catch (e: IndexOutOfBoundsException) {
            println("ОШИБКА!!!!!!!! $e")
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = trackList.size

}

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}