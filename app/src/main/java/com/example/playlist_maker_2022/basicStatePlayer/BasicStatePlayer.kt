package com.example.playlist_maker_2022.basicStatePlayer

import android.media.MediaPlayer
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
import com.example.playlist_maker_2022.searchingActivity.Track
import com.example.playlist_maker_2022.databinding.ActivityBasicStatePlayerBinding
import kotlinx.android.synthetic.main.activity_basic_state_player.*
import java.text.SimpleDateFormat
import java.util.*

class BasicStatePlayer : AppCompatActivity() {

    private lateinit var binding: ActivityBasicStatePlayerBinding
    private lateinit var play: ImageButton
    private var mediaPlayer = MediaPlayer()

    companion object {
        const val TRACK_KEY = "trackKey"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private val handler: Handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicStatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromPlayer.setOnClickListener { finish() }
//      val trackJson = intent.getStringExtra(TRACK_KEY)
//      val track = Gson().fromJson(trackJson, Track::class.java)
        @Suppress("DEPRECATION") val track = intent.getParcelableExtra<Track>(TRACK_KEY)

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
        preparePlayer(url)
        play.setOnClickListener {
            playbackControl()
        }
    }

    private val searchRunnable = object : Runnable {
        override fun run() {
            binding.tvCurrentTimeTrack.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            handler.postDelayed(this, 300)
        }
    }

    private fun preparePlayer(url: String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.bt_play_day)
            binding.tvCurrentTimeTrack.text = getString(R.string.StartTime)
            playerState = STATE_PREPARED
            handler.removeCallbacks(searchRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        //   handler.postDelayed(searchRunnable, 300L)
        handler.post(searchRunnable)
        play.setImageResource(R.drawable.bt_stop_day)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(searchRunnable)
        play.setImageResource(R.drawable.bt_play_day)
        playerState = STATE_PAUSED

    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(searchRunnable)
    }
}