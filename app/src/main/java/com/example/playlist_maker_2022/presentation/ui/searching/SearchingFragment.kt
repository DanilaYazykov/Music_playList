package com.example.playlist_maker_2022.presentation.ui.searching

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.data.network.NetworkResult
import com.example.playlist_maker_2022.databinding.FragmentSearchingBinding
import com.example.playlist_maker_2022.domain.models.Track
import com.example.playlist_maker_2022.presentation.viewModels.searching.SearchViewModel
import com.example.playlist_maker_2022.presentation.ui.player.PlayerFragment
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import com.example.playlist_maker_2022.presentation.util.internetDialogUtil.NoInternetDialogManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchingFragment : BindingFragment<FragmentSearchingBinding>(), OnTrackClickListener {

    internal var text: String = ""
    private var recyclerViewState: Parcelable? = null
    private var recyclerViewPosition = 0
    private val searchingViewModel by viewModel <SearchViewModel> { parametersOf(text) }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchingBinding {
        return FragmentSearchingBinding.inflate(inflater, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val trackAdapter = TrackAdapter(emptyList(), this)
            binding.rcViewSearching.layoutManager = LinearLayoutManager(requireContext())
            binding.rcViewSearching.adapter = trackAdapter

            searchingViewModel.getStateLiveData.observe(viewLifecycleOwner) { search ->
                trackAdapter.trackList = search.trackList.second
                drawTrack(search.trackList)
                trackAdapter.notifyDataSetChanged()
            }
            val searchAdapter = TrackAdapter(emptyList(), this)
            binding.rVSearchHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rVSearchHistory.adapter = searchAdapter
            searchingViewModel.getStateLiveData.observe(viewLifecycleOwner) { history ->
                searchAdapter.trackList = history.searchList
                searchAdapter.notifyDataSetChanged()
                binding.clSearchHistory.visibility = if (history.searchList.isNotEmpty() && text.isBlank())
                    View.VISIBLE else View.GONE
            }

            binding.clearIcon.setOnClickListener {
                binding.inputEditText.setText("")
                hideKeyboard()
            }
            binding.inputEditText.addTextChangedListener(SearchingTextWatcher(this, searchingViewModel))
            searchingViewModel.getStateLiveData.observe(viewLifecycleOwner) { connection ->
                if (!connection.internet) {
                    SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
                    NoInternetDialogManager().internetSettingsDialog(
                        requireContext(), object : NoInternetDialogManager.Listener {
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
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_PROGRESSBAR)
            }

        onRestoreInstanceState(savedInstanceState)
    }

    private fun drawTrack(track: Pair<NetworkResult, List<Track>>) {
        when (track.first) { NetworkResult.SUCCESS -> {
                if (track.second.isNotEmpty()) SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_SEARCHING_RESULT)
                hideKeyboard()
            }
            NetworkResult.TRACKS_NOT_FOUND -> SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_RESULT)
            NetworkResult.ERROR -> {
                SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NO_CONNECTION)
                hideKeyboard()
            }
            NetworkResult.NULL_REQUEST -> SetVisibility(binding).simpleVisibility(SetVisibility.SHOW_NOTHING)
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
        try {
            outState.putParcelable(
                RECYCLER_STATE,
                binding.rcViewSearching.layoutManager?.onSaveInstanceState()
            )
            recyclerViewPosition =
                (binding.rcViewSearching.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
                    ?: 0
            outState.putInt(RECYCLER_POSITION, recyclerViewPosition)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) return
        text = savedInstanceState.getString(TEXT_SEARCH, "")
        @Suppress("DEPRECATION")
        recyclerViewState = savedInstanceState.getParcelable(RECYCLER_STATE)
        recyclerViewPosition = savedInstanceState.getInt(RECYCLER_POSITION, 0)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTrackClick(track: Track) {
        searchingViewModel.onSearchTrackClicked(track)
        if (searchingViewModel.debounceClick()) {
            findNavController().navigate(R.id.action_searchingFragment_to_playerFragment,
                PlayerFragment.createArgs(track))
        }
    }

    companion object {
        const val TEXT_SEARCH = "textSearch"
        const val RECYCLER_STATE = "recycler_state"
        const val RECYCLER_POSITION = "recycler_position"
    }
}