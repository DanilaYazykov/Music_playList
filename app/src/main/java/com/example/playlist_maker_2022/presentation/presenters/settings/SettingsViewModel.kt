package com.example.playlist_maker_2022.presentation.presenters.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker_2022.data.sharedPreferences.ThemeStatus

class SettingsViewModel : ViewModel() {

    private val _switchThemeChecked = MutableLiveData(ThemeStatus.themeStatus)
    val switchThemeChecked: LiveData<Boolean> = _switchThemeChecked

    fun switchTheme(checked: Boolean) {
        ThemeStatus().switchTheme(checked)
        _switchThemeChecked.value = checked
    }
}