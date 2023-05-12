package com.example.playlist_maker_2022.presentation.ui.searching

import android.view.View
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding

class SetVisibility(private val binding: ActivitySearchingBinding) {

    private fun showViews(vararg views: View) {
        binding.rcViewSearching.visibility = View.GONE
        binding.clSearchHistory.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.GONE
        binding.rlProgressBar.visibility = View.GONE
        binding.iwErrorLayout.visibility = View.GONE
        for (view in views) {
            view.visibility = View.VISIBLE
        }
    }

    fun buttonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun simpleVisibility(s: Int?) {
        when (s) {
            SHOW_PROGRESSBAR -> showViews(binding.rlProgressBar)
            SHOW_NO_CONNECTION -> showViews(binding.iwErrorLayout)
            SHOW_SEARCHING_RESULT -> showViews(binding.rcViewSearching)
            SHOW_HISTORY_SEARCHING_RESULT -> showViews(binding.clSearchHistory)
            SHOW_NO_RESULT -> showViews(binding.iwNoResultLayout)
            SHOW_NOTHING -> showViews()
            else -> Unit
        }
    }

    companion object {
        const val SHOW_PROGRESSBAR = 1
        const val SHOW_NO_CONNECTION = 2
        const val SHOW_SEARCHING_RESULT = 3
        const val SHOW_HISTORY_SEARCHING_RESULT = 4
        const val SHOW_NO_RESULT = 5
        const val SHOW_NOTHING = 6
    }
}