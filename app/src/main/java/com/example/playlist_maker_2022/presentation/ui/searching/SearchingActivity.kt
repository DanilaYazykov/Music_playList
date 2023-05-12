package com.example.playlist_maker_2022.presentation.ui.searching

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.util.internetDialogUtil.NoInternetDialogManager
import com.example.playlist_maker_2022.presentation.presenters.searching.*
import com.example.playlist_maker_2022.presentation.ui.player.PlayerActivity

class SearchingActivity : AppCompatActivity(), OnTrackClickListener {

    internal var text: String = ""
    internal lateinit var binding: ActivitySearchingBinding
    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0
    private lateinit var searchingViewModel: SearchViewModel

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backFromSearching.setOnClickListener { finish() }
        searchingViewModel = ViewModelProvider(this, SearchViewModelFactory(track = text))[SearchViewModel::class.java]
        searchingViewModel.getStateLiveData.observe(this) { search ->
                val trackAdapter = TrackAdapter(search.trackList.second, this)
                binding.rcViewSearching.layoutManager = LinearLayoutManager(this@SearchingActivity)
                binding.rcViewSearching.adapter = trackAdapter
                drawTrack(search.trackList)
        }

        searchingViewModel.getStateLiveData.observe(this) { history ->
            val searchAdapter = TrackAdapter(history.searchList, this)
            binding.rVSearchHistory.layoutManager = LinearLayoutManager(this@SearchingActivity)
            binding.rVSearchHistory.adapter = searchAdapter
            binding.clSearchHistory.visibility = if (history.searchList.isNotEmpty() && text.isBlank()) View.VISIBLE else View.GONE
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }
        binding.inputEditText.addTextChangedListener(SearchingTextWatcher(this, searchingViewModel))
        searchingViewModel.getStateLiveData.observe(this) { connection ->
            if (!connection.internet) {
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
                NoInternetDialogManager().internetSettingsDialog(
                    this@SearchingActivity, object : NoInternetDialogManager.Listener {
                        override fun onClick(name: String?) {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        }
                    })
            }
        }
        if (savedInstanceState != null) {
            text = savedInstanceState.getString(TEXT_SEARCH, "")
            @Suppress("DEPRECATION")
            recyclerViewState = savedInstanceState.getParcelable("recyclerViewState")
            recyclerViewPosition = savedInstanceState.getInt("recyclerViewPosition")
            if (text.isNotEmpty()) {
                searchingViewModel.debounceSearch(text)
                binding.inputEditText.setText(text)
            }
        }

        binding.btClearSearch.setOnClickListener {
            searchingViewModel.clearTracks()
        }
        binding.btUpdate.setOnClickListener {
            searchingViewModel.debounceSearch(text)
            SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_PROGRESSBAR)
        }
        binding.btUpdateError.setOnClickListener {
            searchingViewModel.debounceSearch(text)
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            binding.rcViewSearching.layoutManager?.onRestoreInstanceState(recyclerViewState)
            binding.rcViewSearching.scrollToPosition(recyclerViewPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_SEARCH, text)
        outState.putParcelable(
            "recycler_state",
            binding.rcViewSearching.layoutManager?.onSaveInstanceState()
        )
        recyclerViewPosition =
            (binding.rcViewSearching.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
                ?: 0
        outState.putInt("recycler_position", recyclerViewPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(TEXT_SEARCH, "")
        @Suppress("DEPRECATION")
        recyclerViewState = savedInstanceState.getParcelable("recycler_state")
        recyclerViewPosition = savedInstanceState.getInt("recycler_position", 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTrackClick(track: Track) {
        searchingViewModel.onSearchTrackClicked(track)
        if (searchingViewModel.debounceClick()) {
            startActivity(Intent(this, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.TRACK_KEY, track)
            })
        }
    }

    private fun drawTrack(track: Pair<NetworkResult, List<Track>>) {
        when (track.first) {
            NetworkResult.SUCCESS -> if (track.second.isNotEmpty()) SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_SEARCHING_RESULT)
            NetworkResult.TRACKS_NOT_FOUND -> SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_RESULT)
            NetworkResult.ERROR -> SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
            NetworkResult.NULL_REQUEST -> SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NOTHING)
        }
    }

    companion object {
        const val TEXT_SEARCH = "textSearch"
    }
}