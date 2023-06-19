package com.example.playlist_maker_2022.presentation.ui.media.playLists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlist_maker_2022.databinding.FragmentMediaPlaylistBinding
import com.example.playlist_maker_2022.presentation.presenters.media.playLists.PlaylistMediaFragmentViewModel
import com.example.playlist_maker_2022.presentation.ui.media.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistMediaFragment: BindingFragment<FragmentMediaPlaylistBinding>() {

    private val viewModel by viewModel<PlaylistMediaFragmentViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaPlaylistBinding {
        return FragmentMediaPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btAddPlaylist.setOnClickListener {
            viewModel.addPlaylist()
        }
    }
}