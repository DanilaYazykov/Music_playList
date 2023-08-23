package com.example.playlist_maker_2022.presentation.ui.openedPlaylist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.FragmentOpenedPlaylistBinding
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.viewModels.openedPlaylist.OpenedPlaylistViewModel
import com.example.playlist_maker_2022.presentation.viewModels.openedPlaylist.UpdatedScreenState
import com.example.playlist_maker_2022.presentation.ui.player.PlayerFragment
import com.example.playlist_maker_2022.presentation.ui.searching.OnTrackClickListener
import com.example.playlist_maker_2022.presentation.ui.searching.TrackAdapter
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OpenedPlaylistFragment : BindingFragment<FragmentOpenedPlaylistBinding>(), OnTrackClickListener {

    private val playlists: Playlists? by lazy { getParcelable() }
    private val viewModel by viewModel <OpenedPlaylistViewModel>()

    private var dotsSheet: BottomSheetBehavior<ConstraintLayout>? = null
    private var trackSheet: BottomSheetBehavior<ConstraintLayout>? = null

    private val trackAdapter = TrackAdapter(emptyList(), this)
    private var trackDialog: AlertDialog? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOpenedPlaylistBinding {
        return FragmentOpenedPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dotsSheet = BottomSheetBehavior.from(binding.bottomSheetDots.root)
        trackSheet = BottomSheetBehavior.from(binding.albumBottom.root)

        getTracksInPlaylist()
        initListeners()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UpdatedScreenState.Default -> Unit
                    is UpdatedScreenState.ExpandedBottomSheet -> drawScreenWithBottomSheet(state.tracks)
                    is UpdatedScreenState.EmptyBottomSheet -> {
                        showSnack(true)
                        drawScreen(state.playlists, emptyList())
                    }
                    is UpdatedScreenState.EmptyShare -> {
                        showSnack(false)
                        dotsSheet?.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    is UpdatedScreenState.SharedPlaylist -> showApps(state.playlists, state.tracks)
                    is UpdatedScreenState.OptionsMenu -> showBottomSheetDots(state.playlists)
                }
            }
        }
    }

    private fun showSnack(emptyBottomSheet: Boolean) {
        val message =
            if (emptyBottomSheet) getString(R.string.no_tracks_in_playlist)
            else getString(R.string.nothing_to_share)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun drawScreenWithBottomSheet(tracks: List<Track>) {
        drawScreen(playlists!!, tracks)
        trackAdapter.trackList = tracks
        binding.albumBottom.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.albumBottom.recycler.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()
        trackSheet?.isHideable = false
        trackSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun drawScreen(playlists: Playlists, tracks: List<Track>) {
        dotsSheet?.state = BottomSheetBehavior.STATE_HIDDEN
        Glide.with(requireActivity())
            .load(playlists.playlistImage)
            .centerCrop()
            .placeholder(R.drawable.no_reply)
            .into(binding.image)
        with(binding) {
            name.text = playlists.playlistName
            year.text = playlists.playlistDescription
            time.text = getTimeAllTracks(tracks)
            trackCount.text = getTrackCount(playlists.playlistTracksCount)
        }
        if (playlists.playlistTracks.isEmpty()) {
            trackSheet?.isHideable = true
            trackSheet?.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showApps(playlists: Playlists, tracks: List<Track>) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, convertAlbumToString(playlists, tracks))
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share APK")
        activity?.startActivity(chooserIntent)
    }

    private fun convertAlbumToString(playlists: Playlists, tracks: List<Track>): String {
        val sb = StringBuilder()
        sb.append("${playlists.playlistName}\n")
        sb.append("${playlists.playlistDescription}\n")
        sb.append(resources.getQuantityString(
            R.plurals.track_count, playlists.playlistTracksCount, playlists.playlistTracksCount) + "\n\n")
        try {
        for ((index, _) in playlists.playlistTracks.withIndex()) {
            sb.append("${index + 1}. ${tracks[index].artistName} — ${tracks[index].trackName}\n " +
                        "(${SimpleDateFormat("mm:ss",
                            Locale.getDefault()).format(tracks[index].trackTimeMillis.toLong())})", "\n\n"
            )
        }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    private fun getParcelable(): Playlists? {
        val args = arguments
        var todoItem: Playlists? = null
        if (args != null) {
            todoItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getParcelable(PlayerFragment.TRACK_KEY, Playlists::class.java)
            } else {
                @Suppress("DEPRECATION")
                args.getParcelable(PlayerFragment.TRACK_KEY)
            }
        }
        return todoItem
    }

    private fun getTracksInPlaylist() {
        viewModel.getTracksFromPlaylist(playlists!!)
    }

    private fun getTimeAllTracks(list: List<Track>): String {
        val totalMilliseconds = list.fold(0L) { acc, track -> acc + track.trackTimeMillis.toLong() }
        val totalMinutes = (totalMilliseconds / (1000 * 60)).toInt()
        return resources.getQuantityString(R.plurals.minutes, totalMinutes, totalMinutes)
    }

    private fun getTrackCount(count: Int): String {
        return resources.getQuantityString(R.plurals.track_count, count, count)
    }

    private fun exitFromPlaylist() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initListeners() {
        trackAdapter.actionLong = { track ->
            showTrackDialog(track.trackId)
        }
        binding.share.setOnClickListener { viewModel.onSharePressed(playlists!!) }
        binding.dots.setOnClickListener { viewModel.onDotsPressed(playlists!!) }
        exitFromPlaylist()
    }

    private fun showBottomSheetDots(playlists: Playlists) {
        with(binding.bottomSheetDots) {
            item.tvNameOfSongSmall.text = playlists.playlistName
            item.tvNameOfGroupSmall.text = playlists.playlistTracksCount.toString()
            share.setOnClickListener { viewModel.onSharePressed(playlists) }
            remove.setOnClickListener { showPlaylistDialog() }
            change.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_openedPlaylistFragment_to_refactorPlaylist,
                    args = bundleOf(TRACK_KEY to playlists),
                )
            }
            Glide.with(requireActivity()).load(playlists.playlistImage)
                .placeholder(R.drawable.no_reply)
                .into(item.ivAlbumCoverSmall)
        }
        dotsSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showPlaylistDialog() {
        trackDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.remove_playlist))
            .setNegativeButton(R.string.no) { _, _ -> trackDialog?.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ -> removeAlbum() }
            .create()
        trackDialog?.setCanceledOnTouchOutside(false)
        trackDialog?.setCancelable(false)
        trackDialog?.show()
    }

    private fun removeAlbum() {
        viewModel.deletePlaylist(playlists!!)
        findNavController().navigateUp()
    }

    private fun showTrackDialog(trackId: String) {
        trackDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.question_delete_track))
            .setNegativeButton(R.string.no) { _, _ -> trackDialog?.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.deleteTrack(trackId, playlists!!) }
            .create()
        trackDialog?.setCanceledOnTouchOutside(false)
        trackDialog?.setCancelable(false)
        trackDialog?.show()
    }

    override fun onTrackClick(track: Track) {
        findNavController().navigate(
            R.id.action_openedPlaylistFragment_to_playerFragment2,
            PlayerFragment.createArgs(track))
    }

    companion object {
        private const val TRACK_KEY = "trackKey"
        fun createArgs(playlists: Playlists): Bundle =
            bundleOf(TRACK_KEY to playlists)
    }
}