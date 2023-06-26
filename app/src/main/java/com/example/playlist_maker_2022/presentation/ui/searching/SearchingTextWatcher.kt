package com.example.playlist_maker_2022.presentation.ui.searching

import android.text.Editable
import android.text.TextWatcher
import com.example.playlist_maker_2022.presentation.presenters.searching.SearchViewModel

class SearchingTextWatcher(
    private val searchingFragment: SearchingFragment,
    private val presenterViewModel: SearchViewModel
) : TextWatcher {
    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchingFragment.text = s.toString()
        searchingFragment.binding.clearIcon.visibility = SetVisibility(searchingFragment.binding).buttonVisibility(s)
        SetVisibility(searchingFragment.binding).simpleVisibility(SetVisibility.SHOW_PROGRESSBAR)
        if (s != "") {
            presenterViewModel.debounceSearch(searchingFragment.text)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        searchingFragment.binding.rlProgressBar.visibility = SetVisibility(searchingFragment.binding).buttonVisibility(s)
        if (searchingFragment.text.isBlank() && presenterViewModel.getStateLiveData.value?.searchList?.isNotEmpty() == true) {
            SetVisibility(searchingFragment.binding).simpleVisibility(SetVisibility.SHOW_HISTORY_SEARCHING_RESULT)
        }
    }
}