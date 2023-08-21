package com.example.playlist_maker_2022.presentation.ui.media.playLists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.FragmentMediaPlaylistBinding
import com.example.playlist_maker_2022.presentation.presenters.media.playLists.PlaylistMediaFragmentViewModel
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistMediaFragment : BindingFragment<FragmentMediaPlaylistBinding>() {

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
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        val playlistAdapter = PlaylistAdapter(emptyList(), requireContext())
        binding.rcViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcViewPlaylists.adapter = playlistAdapter

        viewModel.getPlaylists()

        viewModel.stateLiveData.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                binding.rcViewPlaylists.visibility = View.GONE
                binding.iwNoPlaylistsLayout.visibility = View.VISIBLE
            } else {
                binding.rcViewPlaylists.visibility = View.VISIBLE
                binding.iwNoPlaylistsLayout.visibility = View.GONE
                playlistAdapter.playlists = playlists
                playlistAdapter.notifyDataSetChanged()
            }
        }
    }
}