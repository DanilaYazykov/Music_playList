package com.example.playlist_maker_2022.presentation.viewModels.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker_2022.data.sharedPreferences.ThemeStatus

class SettingsViewModel(private val themeStatus: ThemeStatus) : ViewModel() {

    private val _switchThemeChecked = MutableLiveData(ThemeStatus.themeStatus)
    val switchThemeChecked: LiveData<Boolean> = _switchThemeChecked

    fun switchTheme(checked: Boolean) {
        themeStatus.switchTheme(checked)
        _switchThemeChecked.value = checked
    }
}