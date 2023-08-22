package com.example.playlist_maker_2022.presentation.ui.media.favouritesSongs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.FragmentMediaFavouritesBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.viewModels.media.favouritesSongs.FavouriteSongFragmentViewModel
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import com.example.playlist_maker_2022.presentation.ui.player.PlayerFragment
import com.example.playlist_maker_2022.presentation.ui.searching.OnTrackClickListener
import com.example.playlist_maker_2022.presentation.ui.searching.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesSongFragment : BindingFragment<FragmentMediaFavouritesBinding>(),
    OnTrackClickListener {

    private val viewModel by viewModel<FavouriteSongFragmentViewModel>()
    private val trackAdapter = TrackAdapter(emptyList(), this)

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaFavouritesBinding {
        return FragmentMediaFavouritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rcViewFavouritesSongs.layoutManager = LinearLayoutManager(requireContext())
            rcViewFavouritesSongs.adapter = trackAdapter
            viewModel.stateLiveData.observe(viewLifecycleOwner) { search ->


                @SuppressLint("NotifyDataSetChanged")
                if (search.isNotEmpty()) {
                    rcViewFavouritesSongs.visibility = View.VISIBLE
                    iwNoFavSongsLayout.visibility = View.GONE
                    trackAdapter.trackList = search
                    trackAdapter.notifyDataSetChanged()
                } else {
                    rcViewFavouritesSongs.visibility = View.GONE
                    iwNoFavSongsLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavouritesTracks()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTrackClick(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }
}