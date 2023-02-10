package com.example.playlist_maker_2022

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker_2022.checkings.CheckingInternet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchingActivity : AppCompatActivity() {

    companion object {
        const val TEXT_SEARCH = "textSearch"
        const val itunesUrl = "https://itunes.apple.com"
    }

    private lateinit var text: String

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searching)

        val buttonBackFromSearching: TextView = findViewById(R.id.backFromSearching)
        buttonBackFromSearching.setOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val trackRcView = findViewById<RecyclerView>(R.id.rcView_searching)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackRcView.adapter = null
            trackRcView.visibility = View.VISIBLE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()

                inputEditText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        responseTracks(text)
                    }
                    false
                }

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_SEARCH, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(TEXT_SEARCH, "")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun responseTracks(searchText: String) {
        val trackRcView = findViewById<RecyclerView>(R.id.rcView_searching)
        trackRcView.layoutManager = LinearLayoutManager(this@SearchingActivity)

        if (searchText.isNotEmpty()) {
            itunesService.search(searchText).enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    val trackList = response.body()?.results
                    val trackAdapter = TrackAdapter(trackList!!)
                    trackRcView.adapter = trackAdapter

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
                    Log.d("onFailure", "onFailure: ${t.message}")
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setVisibility(
        response: Response<TrackResponse>?,
        trackList: List<Track>?,
        trackAdapter: TrackAdapter?
    ) {
        val flRcView = findViewById<RecyclerView>(R.id.rcView_searching)
        val iwSearchNoResult = findViewById<FrameLayout>(R.id.iw_no_result_layout)
        val iwNoConnection = findViewById<FrameLayout>(R.id.iw_no_connection_layout)

        flRcView.visibility = View.VISIBLE

        if (response != null && response.code() == 200) {
            if (trackList != null && trackList.isNotEmpty()) {
                trackAdapter?.notifyDataSetChanged()
            } else {
                flRcView.visibility = View.GONE
                iwNoConnection.visibility = View.GONE
                iwSearchNoResult.visibility = View.VISIBLE
            }
        } else {
            flRcView.visibility = View.GONE
            iwSearchNoResult.visibility = View.GONE
            iwNoConnection.visibility = View.VISIBLE
        }
    }
}