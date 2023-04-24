package com.example.playlist_maker_2022.domain.searching.impl

import android.util.Log
import com.example.playlist_maker_2022.domain.searching.api.TracksInteractor
import com.example.playlist_maker_2022.domain.searching.api.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun likeTrack(trackId: String, consumer: TracksInteractor.TrackInfoConsumer) {
    }

    override fun unlikeTrack(trackId: String, consumer: TracksInteractor.TrackInfoConsumer) {
    }

    override fun getTrackInfo(trackId: String, consumer: TracksInteractor.TrackInfoConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val track = repository.getTrackForId(trackId)
                withContext(Dispatchers.Main) {
                    consumer.consume(track = track)
                }
            } catch (e: java.lang.Exception) {
                Log.e("Exception","Ошибка $e")
            }
        }
    }
}