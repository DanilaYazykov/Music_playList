package com.example.playlist_maker_2022.presentation.ui.searching

import android.text.Editable
import android.text.TextWatcher
import com.example.playlist_maker_2022.presentation.presenters.searching.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchingTextWatcher(
    private val searchingActivity: SearchingActivity,
    private val presenterViewModel: SearchViewModel
) : TextWatcher {
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
            presenterViewModel.debounceSearch(searchingActivity.text)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        searchingActivity.binding.rlProgressBar.visibility = SetVisibility(searchingActivity.binding).buttonVisibility(s)
        if (searchingActivity.text.isBlank() && searchingActivity.searchList.isNotEmpty()) {
            SetVisibility(searchingActivity.binding).simpleVisibility(SetVisibility.SHOW_HISTORY_SEARCHING_RESULT)
        }
        // todo: временное решение. После реализую BroadcastReceiver с проверкой через него
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            presenterViewModel.isNetworkAvailable(application = searchingActivity.application)
        }
    }
}