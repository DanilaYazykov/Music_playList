package com.example.playlist_maker_2022.searchingActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker_2022.*
import com.example.playlist_maker_2022.basicStatePlayer.BasicStatePlayer
import com.example.playlist_maker_2022.checkings.CheckingInternet
import com.example.playlist_maker_2022.databinding.ActivitySearchingBinding
import com.example.playlist_maker_2022.repository.ItunesRepository
import com.example.playlist_maker_2022.repository.ResponseTracks
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_searching.*
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel

class SearchingActivity : AppCompatActivity(), OnTrackClickListener {

    companion object {
        const val TEXT_SEARCH = "textSearch"
    }

    private lateinit var binding: ActivitySearchingBinding
    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0

    private var text: String = ""
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private var trackList = ArrayList<Track>()
    private var searchList = ArrayList<Track>()
    private val itunesService = ItunesRepository().itunesService
    private val debounce by lazy {
        Debounce(
            itunesService, trackList, trackAdapter, binding
        )
    }

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromSearching.setOnClickListener { finish() }
        sharedPrefs = getSharedPreferences("tracks", MODE_PRIVATE)
        val savedBeforeTracks = sharedPrefs.getString("searchTracks", null)
        if (savedBeforeTracks != null) {
            val savedTracks =
                Gson().fromJson(savedBeforeTracks, Array<Track>::class.java)
            searchList.addAll(savedTracks)
        }
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
            if (searchList.isNotEmpty()) {
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_HISTORY_SEARCHING_RESULT)
            }
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()
                binding.clearIcon.visibility = SetVisibility(binding).buttonVisibility(s)
                if (text.isNotEmpty()) {
                    debounce.searchDebounce()
                } else {
                    debounce.searchDebounce().apply {
                        binding.rlProgressBar.visibility = SetVisibility(binding).buttonVisibility(s)
                        @Suppress("DEPRECATION")
                        cancel()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (text.isBlank() && searchList.isNotEmpty()) {
                    SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_HISTORY_SEARCHING_RESULT)
                }
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (CheckingInternet().isNetworkAvailable(this)) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ResponseTracks().responseTracks(
                        text, itunesService, trackList, trackAdapter, binding
                    )
                }
            } else {
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
                CheckingInternet.DialogManager.internetSettingsDialog(
                    this, object : CheckingInternet.DialogManager.Listener {
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
                ResponseTracks().responseTracks(
                    text, itunesService, trackList, trackAdapter, binding
                )
                binding.inputEditText.setText(text)
            }
        }

        binding.btClearSearch.setOnClickListener {
            sharedPrefs.edit()
                .clear()
                .apply()
            searchList.clear()
            searchAdapter.notifyDataSetChanged()
            binding.clSearchHistory.animate()
                .alpha(0f)
                .setDuration(1000)
                .withEndAction {
                    SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_SEARCHING_RESULT)
                    binding.clSearchHistory.visibility = View.GONE
                    binding.clSearchHistory.alpha = 1f
                }
                .start()
        }
        binding.btUpdate.setOnClickListener {
            ResponseTracks().responseTracks(
                text, itunesService, trackList, trackAdapter, binding
            )
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

    @OptIn(DelicateCoroutinesApi::class)
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
        sharedPrefs.edit()
            .putString("searchTracks", Gson().toJson(searchList))
            .apply()
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)
            searchAdapter.notifyDataSetChanged()
            repeat(100) {
                println(it.toString())
            }
        }
        if (debounce.clickDebounce()) {
            val intent = Intent(this, BasicStatePlayer::class.java)
            intent.putExtra(BasicStatePlayer.TRACK_KEY, track)
            startActivity(intent)
        }
    }
}