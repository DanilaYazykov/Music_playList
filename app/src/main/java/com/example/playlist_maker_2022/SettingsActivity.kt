package com.example.playlist_maker_2022

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBackFromSetting: TextView = findViewById(R.id.backFromSetting)
        buttonBackFromSetting.setOnClickListener {
            finish()
        }

        val buttonSwitchTheme: SwitchMaterial = findViewById(R.id.switchThemeMain)
        buttonSwitchTheme.isChecked = App.themeStatus
        buttonSwitchTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        val buttonShareApplication: Button = findViewById(R.id.shareApplication)
        buttonShareApplication.setOnClickListener {
            val shareApplication = Intent(Intent.ACTION_SEND)
            shareApplication.type = "text/plain"
            shareApplication.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumLink))
            startActivity(shareApplication)
        }

        val buttonTehSupport: Button = findViewById(R.id.bt_update)
        buttonTehSupport.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = Uri.parse(getString(R.string.mailTo))
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message))
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messageTheme))
            startActivity(sendIntent)
        }

        val buttonAgreement: Button = findViewById(R.id.goToAgreement)
        buttonAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.practicumOffer))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}

class App : Application() {
companion object {
        var themeStatus = false
        lateinit var sharedPrefs: SharedPreferences
    }

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
}
