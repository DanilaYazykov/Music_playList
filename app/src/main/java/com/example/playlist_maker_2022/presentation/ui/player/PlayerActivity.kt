package com.example.playlist_maker_2022.presentation.ui.player

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.ActivityBasicStatePlayerBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.presenters.player.PlayStatus
import com.example.playlist_maker_2022.presentation.presenters.player.PlayerViewModel
import com.example.playlist_maker_2022.presentation.presenters.player.PlayerViewModelFactory
//import kotlinx.android.synthetic.main.activity_basic_state_player.*
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : ComponentActivity() {

    private lateinit var binding: ActivityBasicStatePlayerBinding
    private lateinit var player: PlayerViewModel
    private var isFavourite = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicStatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromPlayer.setOnClickListener { finish() }
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_KEY)
        }
        player = ViewModelProvider(
            this,
            PlayerViewModelFactory(track = track, application = application)
        )[PlayerViewModel::class.java]
        draw(track)
        binding.abPlay.setOnClickListener {
            player.playbackControl()
        }

        player.getPlayStatusLiveData().observe(this) { playStatus ->
            when (playStatus) {
                is PlayStatus.Default -> {
                    binding.abPlay.setImageResource(R.drawable.bt_play_day)
                    binding.progressBar.visibility = View.VISIBLE
                }
                is PlayStatus.Prepared -> setPlayButtonVisible()
                is PlayStatus.Playing -> {
                    binding.abPlay.setImageResource(R.drawable.bt_stop_day)

                }
                is PlayStatus.Paused -> {
                    binding.abPlay.setImageResource(R.drawable.bt_play_day)

                }
                else -> Unit
            }
        }

        player.getCurrentTimeLiveData().observe(this) { currentTime ->
            binding.tvCurrentTimeTrack.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(currentTime)
        }

        binding.isLikedButton.setOnClickListener {
            isFavourite = !isFavourite
            if (isFavourite) {
                player.likeTrack(track!!)
            } else {
                player.unlikeTrack(track!!)
            }
        }

        player.getLikedLiveData().observe(this) { isLiked ->
            isFavourite = isLiked
            binding.isLikedButton.setImageResource(
                if (isFavourite) {
                    R.drawable.bt_heart_liked
                } else {
                    R.drawable.bt_heart
                }
            )
        }
    }

    private fun draw(track: Track?) {
        player.likeControl(track!!.trackId)
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
        binding.tvCurrentTimeTrack.text = getString(R.string.StartTime)

        @Suppress("DEPRECATION")
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.no_reply)
            .optionalCenterCrop()
            .transform(RoundedCorners(8))
            .into(binding.imageView3)
    }

    private fun setPlayButtonVisible() {
        binding.progressBar.visibility = View.GONE
        binding.abPlay.visibility = View.VISIBLE
        binding.tvCurrentTimeTrack.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) player.onCleared()
    }

    companion object {
        const val TRACK_KEY = "trackKey"
    }
}