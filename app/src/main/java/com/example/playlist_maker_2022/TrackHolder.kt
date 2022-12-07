package com.example.playlist_maker_2022

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val nameOfSong: TextView = itemView.findViewById(R.id.tv_name_of_song)
    private val nameOfGroup: TextView = itemView.findViewById(R.id.tv_name_of_group)
    private val albumCover: ImageView = itemView.findViewById(R.id.iv_album_cover)

    fun bind(track: Track) {
        nameOfSong.text = track.trackName
        nameOfGroup.text = buildSpannedString {
            append(track.artistName)
            append(" • ")
            append(track.trackTime)
        }
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.no_reply)
            .centerCrop()
            .transform(RoundedCorners(5))
            .into(albumCover)
    }

}