package com.example.playlist_maker_2022.presentation.ui.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.playlist_maker_2022.R

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val back = findViewById<TextView>(R.id.tw_backFromMediateka)
        back.setOnClickListener {
            finish()
        }
    }
}