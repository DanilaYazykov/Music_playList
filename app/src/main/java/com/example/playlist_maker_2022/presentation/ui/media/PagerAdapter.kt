package com.example.playlist_maker_2022.presentation.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlist_maker_2022.presentation.ui.media.favouritesSongs.FavouritesSongFragment
import com.example.playlist_maker_2022.presentation.ui.media.playLists.PlaylistMediaFragment

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavouritesSongFragment() else PlaylistMediaFragment()
    }
}