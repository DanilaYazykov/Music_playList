package com.example.playlist_maker_2022.presentation.ui.player

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.ActivityBasicStatePlayerBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.presenters.player.PlayStatus
import com.example.playlist_maker_2022.presentation.presenters.player.PlayerViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicStatePlayerBinding
    private var track: Track? = null
    private val playerViewModel by viewModel<PlayerViewModel>(parameters = { parametersOf(track?.previewUrl) })

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicStatePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromPlayer.setOnClickListener { finish() }
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_KEY)
        }
        draw(track)
        binding.abPlay.setOnClickListener {
            playerViewModel.playbackControl()
        }

        playerViewModel.getPlayerStateLiveData.observe(this) { playerState ->
            when (playerState.playStatus) {
                is PlayStatus.Default -> {
                    binding.abPlay.setImageResource(R.drawable.bt_play_day)
                    binding.progressBar.visibility = View.VISIBLE
                }

                is PlayStatus.Prepared -> setPlayButtonVisible()
                is PlayStatus.Playing -> {
                    binding.abPlay.setImageResource(R.drawable.bt_stop_day)
                    setPlayButtonVisible()
                }

                is PlayStatus.Paused -> {
                    binding.abPlay.setImageResource(R.drawable.bt_play_day)
                    setPlayButtonVisible()
                }
            }

            binding.tvCurrentTimeTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(playerState.currentTime)

            binding.isLikedButton.apply {
                setImageResource(
                    if (playerState.liked) R.drawable.bt_heart_liked
                    else R.drawable.bt_heart
                )
                imageTintList = ColorStateList.valueOf(
                    if (playerState.liked) ContextCompat.getColor(context, R.color.pink)
                    else ContextCompat.getColor(context, R.color.white)
                )
                setOnClickListener {
                    if (playerState.liked) {
                        lifecycleScope.launch { playerViewModel.unlikeTrack(track!!) }
                    } else {
                        lifecycleScope.launch { playerViewModel.likeTrack(track!!) }
                    }
                }
            }
        }
    }

    private fun draw(track: Track?) {
        lifecycleScope.launch {
            playerViewModel.likeControl(track!!.trackId)
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

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
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
        if (isFinishing) playerViewModel.onCleared()
    }

    companion object {
        const val TRACK_KEY = "trackKey"
    }
}