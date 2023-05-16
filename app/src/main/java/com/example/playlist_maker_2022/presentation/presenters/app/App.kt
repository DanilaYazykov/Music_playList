package com.example.playlist_maker_2022.presentation.presenters.app

import android.app.Application
import com.example.playlist_maker_2022.data.sharedPreferences.ThemeStatus
import com.example.playlist_maker_2022.di.dataModule
import com.example.playlist_maker_2022.di.domainModule
import com.example.playlist_maker_2022.di.playerViewModel
import com.example.playlist_maker_2022.di.searchingViewModel
import com.example.playlist_maker_2022.di.settingsViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    private val themeStatus by inject<ThemeStatus>()

    override fun onCreate() {
        super.onCreate()
        initKoin()
        themeStatus.switchTheme(ThemeStatus.themeStatus)
    }

    private fun initKoin() {
        startKoin {
            androidLogger(level = org.koin.core.logger.Level.DEBUG)
            androidContext(this@App)
            modules(listOf(domainModule, settingsViewModel, dataModule, searchingViewModel, playerViewModel))
        }
    }
}