package com.example.playlist_maker_2022

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker_2022.checkings.CheckingInternet
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchingActivity : AppCompatActivity(), OnTrackClickListener {

    companion object {
        const val TEXT_SEARCH = "textSearch"
        const val itunesUrl = "https://itunes.apple.com"
    }

    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0

    private var text: String = ""
    private lateinit var clearSearchButton: Button
    private lateinit var searchHistory: ConstraintLayout
    private lateinit var trackRcView: RecyclerView
    private lateinit var searchRcView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var iwSearchNoResult: FrameLayout
    private lateinit var iwNoConnection: FrameLayout

    private var trackList = ArrayList<Track>()
    private var searchList = ArrayList<Track>()

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searching)

        val buttonBackFromSearching: TextView = findViewById(R.id.backFromSearching)
        buttonBackFromSearching.setOnClickListener {
            finish()
        }
        iwSearchNoResult = findViewById(R.id.iw_no_result_layout)
        iwNoConnection = findViewById(R.id.iw_no_connection_layout)
        searchHistory = findViewById(R.id.cl_search_history)
        searchRcView = findViewById(R.id.rV_search_history)
        trackRcView = findViewById(R.id.rcView_searching)
        sharedPrefs = getSharedPreferences("tracks", MODE_PRIVATE)
        val savedBeforeTracks = sharedPrefs.getString("searchTracks", null)
        if (savedBeforeTracks != null) {
            val savedTracks =
                Gson().fromJson(savedBeforeTracks, Array<Track>::class.java)
            searchList.addAll(savedTracks)
        }
        searchRcView.layoutManager = LinearLayoutManager(this@SearchingActivity)

        searchAdapter = TrackAdapter(searchList, this)
        searchRcView.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()
        trackRcView.layoutManager = LinearLayoutManager(this@SearchingActivity)

        trackAdapter = TrackAdapter(trackList, this)
        trackRcView.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()


        if (searchList.isNotEmpty()) {
            searchHistory.visibility = View.VISIBLE
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            trackRcView.visibility = View.GONE
            iwSearchNoResult.visibility = View.GONE
            iwNoConnection.visibility = View.GONE
            if (searchList.isNotEmpty()) {
                searchHistory.visibility = View.VISIBLE
            }
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
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
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchHistory.visibility = View.GONE
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
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
                inputEditText.setText(text)
            }
        }

        clearSearchButton = findViewById(R.id.bt_clear_search)
        clearSearchButton.setOnClickListener {
            sharedPrefs.edit()
                .clear()
                .apply()
            searchList.clear()
            searchAdapter.notifyDataSetChanged()
            searchHistory.animate()
                .alpha(0f)
                .setDuration(1000)
                .withEndAction {
                    searchHistory.visibility = View.GONE
                    searchHistory.alpha = 1f
                }
                .start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            trackRcView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            trackRcView.scrollToPosition(recyclerViewPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_SEARCH, text)
        outState.putParcelable("recycler_state", trackRcView.layoutManager?.onSaveInstanceState())
        recyclerViewPosition =
            (trackRcView.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun responseTracks(searchText: String) {

        if (searchText.isNotEmpty()) {
            itunesService.search(searchText).enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    trackRcView.visibility = View.VISIBLE
                    trackList.clear()
                    response.body()?.results?.let { trackList.addAll(it) }
                    trackAdapter.notifyDataSetChanged()
                    setVisibility(response, trackList, trackAdapter)
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    setVisibility(null, null, null)
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

                    val updateButton = findViewById<Button>(R.id.bt_update)
                    updateButton.setOnClickListener {
                        responseTracks(text)
                    }
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setVisibility(
        response: Response<TrackResponse>?,
        trackList: ArrayList<Track>?,
        trackAdapter: TrackAdapter?
    ) {
        if (response != null && response.code() == 200) {
            if (trackList != null && trackList.isNotEmpty()) {
                response.body()?.results?.let { trackList.addAll(it) }
                trackAdapter?.notifyDataSetChanged()
            } else {
                trackRcView.visibility = View.GONE
                iwNoConnection.visibility = View.GONE
                iwSearchNoResult.visibility = View.VISIBLE
            }
        } else {
            trackRcView.visibility = View.GONE
            iwSearchNoResult.visibility = View.GONE
            iwNoConnection.visibility = View.VISIBLE
        }
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
        sharedPrefs.edit()
            .putString("searchTracks", Gson().toJson(searchList))
            .apply()
        searchAdapter.notifyDataSetChanged()
    }
}