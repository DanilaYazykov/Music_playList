package com.example.playlist_maker_2022.presentation.ui.player

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.data.PlayerBasicImpl
import com.example.playlist_maker_2022.databinding.ActivityBasicStatePlayerBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.impl.PlayerInteractorImpl
import com.example.playlist_maker_2022.presentation.presenters.player.PlayerPresenter
import com.example.playlist_maker_2022.presentation.presenters.player.PlayerView
import kotlinx.android.synthetic.main.activity_basic_state_player.*
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity(), PlayerView {

    private lateinit var binding: ActivityBasicStatePlayerBinding
    private lateinit var play: ImageButton
    private lateinit var player: PlayerPresenter


    private val handler: Handler = Handler(Looper.getMainLooper())

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

        binding.tvNameOfSong.text = track?.trackName
        binding.tvNameOfGroup.text = track?.artistName
        binding.tvNameOfAlbum.text = track?.collectionName
        binding.tvTrackTime.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(track?.trackTimeMillis?.toLong())
        binding.tvReleaseYear.text = track?.releaseDate?.substring(0, 4)
        binding.tvGenreName.text = track?.primaryGenreName
        binding.tvCountryName.text = track?.country
        binding.tvCurrentTimeTrack.text = getString(R.string.StartTime)
        val url = track?.previewUrl

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.no_reply)
            .optionalCenterCrop()
            .transform(RoundedCorners(8))
            .into(binding.imageView3)

        play = findViewById(R.id.ab_play)

        val playerInteractor = PlayerInteractorImpl(PlayerBasicImpl(url))
        player = PlayerPresenter(playerInteractor, this, handler)
        preparePlayer()
        play.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer() {
        play.isEnabled = true
        player.preparePlayer()
    }

    private fun playbackControl() {
        player.playbackControl()
    }

    override fun onPause() {
        super.onPause()
        player.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.destroy()
    }

    override fun setButtonToPlay() {
        play.setImageResource(R.drawable.bt_play_day)
    }

    override fun setButtonToPause() {
        play.setImageResource(R.drawable.bt_stop_day)
    }

    override fun setStartTime() {
        binding.tvCurrentTimeTrack.text = getString(R.string.StartTime)
    }

    override fun setCurrentTime() {
        binding.tvCurrentTimeTrack.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.getCurrentPosition())
    }

    companion object {
        const val TRACK_KEY = "trackKey"
        const val delayMillis = 1000L
    }
}