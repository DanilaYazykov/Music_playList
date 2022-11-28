package com.example.playlist_maker_2022

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch: Button = findViewById(R.id.search)
        buttonSearch.setOnClickListener {
            val intent = Intent(this, SearchingActivity::class.java)
            startActivity(intent)
        }

        val buttonMedia = findViewById<Button>(R.id.media)
        buttonMedia.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        val buttonSettings = findViewById<Button>(R.id.setting)
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}