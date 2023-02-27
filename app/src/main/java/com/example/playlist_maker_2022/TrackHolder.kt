package com.example.playlist_maker_2022

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val nameOfSong: TextView = itemView.findViewById(R.id.tv_name_of_song)
    private val nameOfGroup: TextView = itemView.findViewById(R.id.tv_name_of_group)
    private val albumCover: ImageView = itemView.findViewById(R.id.iv_album_cover)

    fun bind(track: Track) {
        nameOfSong.text = track.trackName

/*      Текст песни не помещается в TextView, поэтому сделал так, чтобы он прокручивался
        Чтобы не перегружать приложение, пока в разметке TextView указал android:ellipsize="end"
        lateinit var animator: ObjectAnimator
        if (nameOfSong.length() > 40) {
            nameOfSong.setOnClickListener {
                animator = ObjectAnimator.ofInt(nameOfSong, "scrollX", 0, 200)
                animator.duration = 3000
                animator.repeatCount = 1
                animator.repeatMode = ObjectAnimator.REVERSE
                animator.start()
            }
        }*/

        nameOfGroup.text = buildSpannedString {
            append(track.artistName)
            append(" • ")
            append(
                // поймал ошибку NumberFormatException, поэтому добавил проверку.
                // вводил в поисковике "James"
                if (track.trackTimeMillis != null) {
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(track.trackTimeMillis.toLong()) } else "00:00"
            )
        }
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.no_reply)
            .centerCrop()
            .transform(RoundedCorners(5))
            .into(albumCover)
    }
}