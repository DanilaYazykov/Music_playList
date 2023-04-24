package com.example.playlist_maker_2022.presentation.ui.searching

import android.text.Editable
import android.text.TextWatcher
import com.example.playlist_maker_2022.presentation.presenters.searching.CreatorTrackPresenter

class SearchingTextWatcher(private val searchingActivity: SearchingActivity) : TextWatcher {
    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchingActivity.text = s.toString()
        searchingActivity.binding.clearIcon.visibility = SetVisibility(searchingActivity.binding).buttonVisibility(s)
        searchingActivity.trackList.clear()
        SetVisibility(searchingActivity.binding).simpleVisibility(SetVisibility.SHOW_PROGRESSBAR)
        if (s != "") {
            CreatorTrackPresenter.provideSearchDebounce(
                track = searchingActivity.text,
                view = searchingActivity
            )
        }
    }

    override fun afterTextChanged(s: Editable?) {
        searchingActivity.binding.rlProgressBar.visibility = SetVisibility(searchingActivity.binding).buttonVisibility(s)
        if (searchingActivity.text.isBlank() && searchingActivity.searchList.isNotEmpty()) {
            SetVisibility(searchingActivity.binding).simpleVisibility(SetVisibility.SHOW_HISTORY_SEARCHING_RESULT)
        }
    }
}