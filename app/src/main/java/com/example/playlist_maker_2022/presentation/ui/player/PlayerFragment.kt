package com.example.playlist_maker_2022.presentation.ui.player

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.FragmentPlayerBinding
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.presenters.player.PlayStatus
import com.example.playlist_maker_2022.presentation.presenters.player.PlayerViewModel
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class PlayerFragment : BindingFragment<FragmentPlayerBinding>(), OnPlayListClickListener {

    private var track: Track? = null
    private val playerViewModel by viewModel<PlayerViewModel>(parameters = { parametersOf(track?.previewUrl) })
    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFromPlayer.setOnClickListener { findNavController().navigateUp() }
        track = getParcelable()
        draw(track)
        binding.abPlay.setOnClickListener {
            playerViewModel.playbackControl()
        }

        playerViewModel.playerStateLiveData.observe(viewLifecycleOwner) { playerState ->
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

        bottomSheetManager()
        addPlaylist()

        val playlistAdapter = PlaylistAdapterSmall(emptyList(), requireContext(), this)
        binding.rcPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.rcPlaylists.adapter = playlistAdapter

        playerViewModel.stateLiveData.observe(viewLifecycleOwner) { playlists ->
            Log.e("PlayerFragment", "onViewCreated: $playlists")
            playlistAdapter.playlists = playlists
            playlistAdapter.notifyDataSetChanged()
        }


    }

    override fun onResume() {
        super.onResume()
        playerViewModel.getPlaylists()
    }

    private fun addPlaylist() {
        binding.btAddPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }
    }

    private fun getState(): Boolean = playerViewModel.insertOrNotFlow.value

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

    private fun bottomSheetManager() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            binding.dimViewBack.visibility = View.GONE
        }

        binding.rcPlaylists.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.dimViewBack.visibility = View.VISIBLE
        }

        binding.addToFavouriteList.setOnClickListener {
            playerViewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.dimViewBack.visibility = View.VISIBLE
        }

        binding.dimViewBack.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.dimViewBack.visibility = View.GONE
        }
        bottomSheetCallBack(bottomSheetBehavior)
    }

    private fun bottomSheetCallBack(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> Unit
                    BottomSheetBehavior.STATE_COLLAPSED -> Unit
                    BottomSheetBehavior.STATE_HIDDEN -> binding.dimViewBack.visibility = View.GONE
                    else -> Unit
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
    }

    private fun getParcelable(): Track? {
        val args = arguments
        var todoItem: Track? = null
        if (args != null) {
            todoItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getParcelable(TRACK_KEY, Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                args.getParcelable(TRACK_KEY)
            }
        }
        return todoItem
    }

    private fun hideBottomSheet() {
        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showToastSuccessOrNot(result: Boolean, playlists: Playlists) {
        val message = if (result) {
            "Трек уже добавлен в плейлист " + " \"" + playlists.playlistName + "\" "
        } else {
            "Добавлено в плейлист " + " \"" + playlists.playlistName + "\" "
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        if (!result) hideBottomSheet()
     }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onCleared()
    }

    override fun onPlaylistClick(playlists: Playlists) {
        playerViewModel.checkPlaylistsAndInsert(playlists, track!!)
        showToastSuccessOrNot(getState(), playlists)
    }

    companion object {
        const val TRACK_KEY = "trackKey"

        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK_KEY to track)
    }
}