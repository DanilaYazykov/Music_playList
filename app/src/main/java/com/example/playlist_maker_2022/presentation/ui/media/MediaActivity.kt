package com.example.playlist_maker_2022.presentation.ui.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlist_maker_2022.R

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MediaFragment.newInstance())
                .commit()
        }
    }
}