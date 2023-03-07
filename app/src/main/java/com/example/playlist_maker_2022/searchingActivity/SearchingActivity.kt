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
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchingActivity : AppCompatActivity(), OnTrackClickListener {

    companion object {
        const val TEXT_SEARCH = "textSearch"
    }
    lateinit var bdnFun: ActivitySearchingBinding

    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0

    private var text: String = ""
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var sharedPrefs: SharedPreferences

    private var trackList = ArrayList<Track>()
    private var searchList = ArrayList<Track>()

    private val itunesService = ItunesRepository().itunesService

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bdnFun = ActivitySearchingBinding.inflate(layoutInflater)
        setContentView(bdnFun.root)

        bdnFun.backFromSearching.setOnClickListener { finish() }
        sharedPrefs = getSharedPreferences("tracks", MODE_PRIVATE)
        val savedBeforeTracks = sharedPrefs.getString("searchTracks", null)
        if (savedBeforeTracks != null) {
            val savedTracks =
                Gson().fromJson(savedBeforeTracks, Array<Track>::class.java)
            searchList.addAll(savedTracks)
        }
        bdnFun.rVSearchHistory.layoutManager = LinearLayoutManager(this@SearchingActivity)

        searchAdapter = TrackAdapter(searchList, this)
        bdnFun.rVSearchHistory.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()
        bdnFun.rcViewSearching.layoutManager = LinearLayoutManager(this@SearchingActivity)

        trackAdapter = TrackAdapter(trackList, this)
        bdnFun.rcViewSearching.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()


        if (searchList.isNotEmpty()) {
            bdnFun.clSearchHistory.visibility = View.VISIBLE
        }

        bdnFun.clearIcon.setOnClickListener {
            bdnFun.inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            bdnFun.rcViewSearching.visibility = View.GONE
            bdnFun.iwNoResultLayout.visibility = View.GONE
            bdnFun.iwNoConnectionLayout.visibility = View.GONE
            if (searchList.isNotEmpty()) {
                bdnFun.clSearchHistory.visibility = View.VISIBLE
            }
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(bdnFun.inputEditText.windowToken, 0)
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
                bdnFun.clearIcon.visibility = SetVisibility(bdnFun).clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                bdnFun.clSearchHistory.visibility = View.GONE
            }
        }
        bdnFun.inputEditText.addTextChangedListener(simpleTextWatcher)
        bdnFun.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                responseTracks(text)
            }
            false
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(TEXT_SEARCH, "")
            @Suppress("DEPRECATION")
            recyclerViewState = savedInstanceState.getParcelable("recyclerViewState")
            recyclerViewPosition = savedInstanceState.getInt("recyclerViewPosition")
            if (text.isNotEmpty()) {
                responseTracks(text)
                bdnFun.inputEditText.setText(text)
            }
        }

        bdnFun.btClearSearch.setOnClickListener {
            sharedPrefs.edit()
                .clear()
                .apply()
            searchList.clear()
            searchAdapter.notifyDataSetChanged()
            bdnFun.clSearchHistory.animate()
                .alpha(0f)
                .setDuration(1000)
                .withEndAction {
                    bdnFun.clSearchHistory.visibility = View.GONE
                    bdnFun.clSearchHistory.alpha = 1f
                }
                .start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            bdnFun.rcViewSearching.layoutManager?.onRestoreInstanceState(recyclerViewState)
            bdnFun.rcViewSearching.scrollToPosition(recyclerViewPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_SEARCH, text)
        outState.putParcelable(
            "recycler_state",
            bdnFun.rcViewSearching.layoutManager?.onSaveInstanceState()
        )
        recyclerViewPosition =
            (bdnFun.rcViewSearching.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
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

    private fun responseTracks(searchText: String) {

        if (searchText.isNotEmpty()) {
            itunesService?.search(searchText)?.enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    bdnFun.rcViewSearching.visibility = View.VISIBLE
                    trackList.clear()
                    response.body()?.results?.let { trackList.addAll(it) }
                    trackAdapter.notifyDataSetChanged()
                    SetVisibility(bdnFun).setVisibility(response, trackList, trackAdapter)
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    SetVisibility(bdnFun).setVisibility(null, null, null)
                    val checkingConnection = CheckingInternet()
                    if (!checkingConnection.isNetworkAvailable(this@SearchingActivity)) {
                        CheckingInternet.DialogManager.internetSettingsDialog(
                            this@SearchingActivity,
                            object : CheckingInternet.DialogManager.Listener {
                                override fun onClick(name: String?) {
                                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                }
                            })
                    }
                    bdnFun.btUpdate.setOnClickListener { responseTracks(text) }
                }
            })
        }
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
        }
        val intent = Intent(this, BasicStatePlayer::class.java)
        intent.putExtra("trackKey", Gson().toJson(track))
        startActivity(intent)
    }
}