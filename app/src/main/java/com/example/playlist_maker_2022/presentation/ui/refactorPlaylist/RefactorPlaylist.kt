package com.example.playlist_maker_2022.presentation.ui.refactorPlaylist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.presentation.viewModels.refactorPlaylist.RefactorPlaylistViewModel
import com.example.playlist_maker_2022.presentation.ui.media.createNewPlaylist.NewPlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RefactorPlaylist : NewPlaylistFragment() {

    override val viewModel by viewModel<RefactorPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawScreen(playlists = playlists!!)
    }

    private fun drawScreen(playlists: Playlists) {
        binding.apply {
            playlistName.setText(playlists.playlistName)
            playlistDescription.setText(playlists.playlistDescription)
            buttonCreate.text = activity?.getString(R.string.save)
            backFromNewPlaylist.text = getString(R.string.refactor)
            ivAddPicture.visibility = View.GONE
            viewModel.setPlaylistId(playlists.playlistId)
            imageBox.setOnClickListener { requestPermissionAndPickImage() }
        }
        Glide.with(this)
            .load(playlists.playlistImage)
            .placeholder(R.drawable.no_reply)
            .transform(RoundedCorners(8))
            .into(binding.imageBox)
    }

    override fun createButtonBehavior() {
        super.createButtonBehavior()
        binding.buttonCreate.setOnClickListener {
            viewModel.updatePlaylist(playlists!!)
            findNavController().navigateUp()
        }
    }

    override fun exitBehavior() {
        if (playlists?.playlistName!!.isNotEmpty()) {
            findNavController().navigateUp()
        }
    }

}