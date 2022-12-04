package com.example.playlist_maker_2022

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBackFromSetting: TextView = findViewById(R.id.backFromSetting)
        buttonBackFromSetting.setOnClickListener {
            onBackPressed()
        }

        val buttonShareApplication: Button = findViewById(R.id.shareApplication)
        buttonShareApplication.setOnClickListener {
            val shareApplication = Intent(Intent.ACTION_SEND)
            shareApplication.type = "text/plain"
            shareApplication.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumLink))
            startActivity(shareApplication)
        }

        val buttonTehSupport: Button = findViewById(R.id.writeToTehSupport)
        buttonTehSupport.setOnClickListener{
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = Uri.parse("mailto:")
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("BigD90@ya.ru"))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message))
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messageTheme))
            startActivity(sendIntent)
        }

        val buttonAgreement: Button = findViewById(R.id.goToAgreement)
        buttonAgreement.setOnClickListener{
            val url = Uri.parse(getString(R.string.practicumOffer))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}