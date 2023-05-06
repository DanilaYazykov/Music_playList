package com.example.playlist_maker_2022.presentation.ui.searching

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.presenters.internetDialogManagerUseCase.NoInternetDialogManager
import com.example.playlist_maker_2022.presentation.presenters.searching.*
import com.example.playlist_maker_2022.presentation.ui.player.PlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchingActivity : ComponentActivity(), OnTrackClickListener {

    internal var text: String = ""
    internal lateinit var binding: ActivitySearchingBinding
    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter
    internal var trackList = ArrayList<Track>()
    internal var searchList = ArrayList<Track>()
    private var isInternetDialogShown = false
    private lateinit var presenterViewModel: SearchViewModel

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backFromSearching.setOnClickListener { finish() }
        presenterViewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(track = text, application = application)
        )[SearchViewModel::class.java]
        presenterViewModel.getTrackLiveData().observe(this) { track -> drawTrack(track) }
        presenterViewModel.getSavedTracks()

        binding.rVSearchHistory.layoutManager = LinearLayoutManager(this@SearchingActivity)
        searchAdapter = TrackAdapter(searchList, this)
        binding.rVSearchHistory.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()

        presenterViewModel.getSearchListLiveData().observe(this) { tracks ->
            searchList.clear()
            searchList.addAll(tracks)
            binding.clSearchHistory.visibility =
                if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }
        binding.rcViewSearching.layoutManager = LinearLayoutManager(this@SearchingActivity)
        trackAdapter = TrackAdapter(trackList, this)
        binding.rcViewSearching.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }
        binding.inputEditText.addTextChangedListener(SearchingTextWatcher(this, presenterViewModel))
        presenterViewModel.getInternetLiveData().observe(this@SearchingActivity) { isInternet ->
            if (!isInternet && !isInternetDialogShown) {
                isInternetDialogShown = true
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
                NoInternetDialogManager().internetSettingsDialog(
                    this@SearchingActivity, object : NoInternetDialogManager.Listener {
                        override fun onClick(name: String?) {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            isInternetDialogShown = false
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
                trackList.clear()
                presenterViewModel.debounceSearch(text)
                binding.inputEditText.setText(text)
            }
        }

        binding.btClearSearch.setOnClickListener {
            presenterViewModel.clearTracks()
            searchAdapter.notifyDataSetChanged()
            SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_SEARCHING_RESULT)
        }
        binding.btUpdate.setOnClickListener {
            trackList.clear()
            presenterViewModel.debounceSearch(text)
            SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_PROGRESSBAR)
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
        presenterViewModel.onSearchTrackClicked(track)
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            searchAdapter.notifyDataSetChanged()
            trackAdapter.notifyDataSetChanged()
        }
        if (presenterViewModel.debounceClick()) {
            startActivity(Intent(this, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.TRACK_KEY, track)
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun drawTrack(track: Pair<NetworkResult, List<Track>>) {
        when (track.first) {
            NetworkResult.SUCCESS -> {
                trackList.addAll(track.second)
                binding.rcViewSearching.visibility = View.VISIBLE
                trackAdapter.notifyDataSetChanged()
            }
            NetworkResult.TRACKS_NOT_FOUND -> {
                binding.iwNoResultLayout.visibility = View.VISIBLE
            }
            NetworkResult.ERROR -> {
                binding.iwNoConnectionLayout.visibility = View.VISIBLE
            }
            NetworkResult.NULL_REQUEST -> Unit
        }
    }

    companion object {
        const val TEXT_SEARCH = "textSearch"
    }
}