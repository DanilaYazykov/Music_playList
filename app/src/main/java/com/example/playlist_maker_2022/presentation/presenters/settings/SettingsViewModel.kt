package com.example.playlist_maker_2022.presentation.presenters.settings

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.data.sharedPreferences.ThemeStatus

class SettingsViewModel(private var application: Application) : AndroidViewModel(application) {

    private val _switchThemeChecked = MutableLiveData(ThemeStatus.themeStatus)
    val switchThemeChecked: LiveData<Boolean> = _switchThemeChecked

    fun switchTheme(checked: Boolean) {
        (application as ThemeStatus).switchTheme(checked)
        _switchThemeChecked.value = checked
    }

    fun shareApplication() {
        val shareApplication = Intent(Intent.ACTION_SEND)
        shareApplication.type = "text/plain"
        shareApplication.putExtra(Intent.EXTRA_TEXT, application.getString(R.string.practicumLink))
        shareApplication.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(shareApplication)
    }

    fun sendEmail() {
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse(application.getString(R.string.mailTo))
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(application.getString(R.string.email)))
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, application.getString(R.string.message))
        sendIntent.putExtra(Intent.EXTRA_TEXT, application.getString(R.string.messageTheme))
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(sendIntent)
    }

    fun goToAgreement() {
        val url = Uri.parse(application.getString(R.string.practicumOffer))
        val intent = Intent(Intent.ACTION_VIEW, url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)
    }
}