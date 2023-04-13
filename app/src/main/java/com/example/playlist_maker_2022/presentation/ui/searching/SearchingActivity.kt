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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.domain.searching.impl.DebounceInteractorImpl
import com.example.playlist_maker_2022.presentation.presenters.searching.*
import com.example.playlist_maker_2022.presentation.presenters.sharedPreferences.SharedPreferencesPresenter
import com.example.playlist_maker_2022.presentation.ui.player.BasicStatePlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchingActivity : AppCompatActivity(), TrackView, OnTrackClickListener {

    companion object {
        const val TEXT_SEARCH = "textSearch"
    }

    internal lateinit var binding: ActivitySearchingBinding
    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0
    internal var text: String = ""
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter
    internal var trackList = ArrayList<Track>()
    internal var searchList = ArrayList<Track>()
    internal val debounce: Debounce = DebouncePresenter(DebounceInteractorImpl())
    private val presenter by lazy { CreatorTrackPresenter.providePresenter(view = this, trackId = text) }

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromSearching.setOnClickListener {
            finish()
        }
        searchList.addAll(SharedPreferencesPresenter(this).getSavedTracks())

        binding.rVSearchHistory.layoutManager = LinearLayoutManager(this@SearchingActivity)
        searchAdapter = TrackAdapter(searchList, this)
        binding.rVSearchHistory.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()

        binding.rcViewSearching.layoutManager = LinearLayoutManager(this@SearchingActivity)
        trackAdapter = TrackAdapter(trackList, this)
        binding.rcViewSearching.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()

        if (searchList.isNotEmpty()) {
            binding.clSearchHistory.visibility = View.VISIBLE
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }

        binding.inputEditText.addTextChangedListener(SearchingTextWatcher(this))
        binding.inputEditText.setOnEditorActionListener { _, _, _ ->
                if (!CheckingInternetPresenter(this).isNetworkAvailable()) {
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
                CreatorNoInternetDialogManager.internetSettingsDialog(
                    this, object : CreatorNoInternetDialogManager.Listener {
                        override fun onClick(name: String?) {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        }
                    })
            }
            false
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(TEXT_SEARCH, "")
            @Suppress("DEPRECATION")
            recyclerViewState = savedInstanceState.getParcelable("recyclerViewState")
            recyclerViewPosition = savedInstanceState.getInt("recyclerViewPosition")
            if (text.isNotEmpty()) {
                trackList.clear()
                presenter
                binding.inputEditText.setText(text)
            }
        }

        binding.btClearSearch.setOnClickListener {
            SharedPreferencesPresenter(this).clearTracks()
            searchList.clear()
            searchAdapter.notifyDataSetChanged()
            SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_SEARCHING_RESULT)
        }
        binding.btUpdate.setOnClickListener {
            trackList.clear()
            presenter
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
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
        val existingTrack = searchList.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            searchList.remove(existingTrack)
            searchList.add(0, existingTrack)
        } else {
            searchList.add(0, track)
        }
        if (searchList.size > 10) {
            searchList.removeLast()
        }
        SharedPreferencesPresenter(this).saveTracks(searchList)
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            searchAdapter.notifyDataSetChanged()
        }
        if (debounce.provideDebounce()) {
            val intent = Intent(this, BasicStatePlayerActivity::class.java)
            intent.putExtra(BasicStatePlayerActivity.TRACK_KEY, track)
            startActivity(intent)
        }
    }

    override fun updateTrackLiked(liked: Boolean) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun drawTrack(track: Pair<NetworkResult, List<Track>>) {
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
            NetworkResult.NULL_REQUEST -> {}
        }
    }
}