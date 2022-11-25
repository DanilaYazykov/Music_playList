package com.example.playlist_maker_2022

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
        val buttonMediaSetOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity, MediaActivity::class.java)
                startActivity(intent)
            }
        }
        buttonMedia.setOnClickListener(buttonMediaSetOnClickListener)

        val buttonSettings = findViewById<Button>(R.id.setting)
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

}