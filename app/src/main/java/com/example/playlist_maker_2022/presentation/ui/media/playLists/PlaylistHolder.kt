package com.example.playlist_maker_2022.presentation.ui.media.playLists

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Playlist

class PlaylistHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

    private val nameOfPlaylist: TextView?  = itemView.findViewById(R.id.title)
    private val countOfTrack: TextView? = itemView.findViewById(R.id.tracksCount)
    private val image: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(playlist: Playlist) {
        nameOfPlaylist?.text = playlist.playlistName
        countOfTrack?.text = mapper(playlist.playlistTracksCount)

        Glide.with(itemView)
            .load(playlist.playlistImage)
            .placeholder(R.drawable.no_reply)
            .transform(CenterCrop(), RoundedCorners(8))
            .addListener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("Glide", "Error loading image", e)
                    return true
                }
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(image)
    }

    private fun mapper(countOfTracks: Int): String {
        return context.resources.getQuantityString(R.plurals.track_count, countOfTracks, countOfTracks)
    }

}