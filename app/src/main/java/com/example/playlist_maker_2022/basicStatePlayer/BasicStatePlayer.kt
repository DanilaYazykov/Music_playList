package com.example.playlist_maker_2022.basicStatePlayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.Track
import com.example.playlist_maker_2022.databinding.ActivityBasicStatePlayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class BasicStatePlayer : AppCompatActivity() {

    private lateinit var binding: ActivityBasicStatePlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicStatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromPlayer.setOnClickListener { finish() }

        val trackJson = intent.getStringExtra("trackKey")
        val track = Gson().fromJson(trackJson, Track::class.java)

        binding.tvNameOfSong.text = track.trackName
        binding.tvNameOfGroup.text = track.artistName
        binding.tvNameOfAlbum.text = track.collectionName
        binding.tvTrackTime.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(track.trackTimeMillis.toLong())
        binding.tvReleaseYear.text = track.releaseDate.substring(0, 4)
        binding.tvGenreName.text = track.primaryGenreName
        binding.tvCountryName.text = track.country
        binding.tvCurrentTimeTrack.text = binding.tvTrackTime.text //прогресс произведения

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.no_reply)
            .optionalCenterCrop()
            .into(binding.imageView3)
    }
}