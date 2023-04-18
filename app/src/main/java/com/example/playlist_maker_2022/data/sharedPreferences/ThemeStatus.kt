package com.example.playlist_maker_2022.data.sharedPreferences

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeStatus : Application() {

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences("theme", MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean("ThemeStatus", false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeStatus = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit().putBoolean("ThemeStatus", themeStatus).apply()
    }

    companion object {
        var themeStatus = false
        lateinit var sharedPrefs: SharedPreferences
    }
}