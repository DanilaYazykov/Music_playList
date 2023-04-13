package com.example.playlist_maker_2022.presentation.presenters.searching

import android.content.Context
import com.example.playlist_maker_2022.domain.searching.impl.CheckingInternetUseCases

class CheckingInternetPresenter(val context: Context?) {

        private val checkingInternet = CheckingInternetUseCases()

        fun isNetworkAvailable(): Boolean {
            return checkingInternet.isNetworkAvailable(context)
        }
}