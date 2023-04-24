package com.example.playlist_maker_2022.presentation.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlist_maker_2022.databinding.ActivityMainBinding
import com.example.playlist_maker_2022.presentation.ui.media.MediaActivity
import com.example.playlist_maker_2022.presentation.ui.searching.SearchingActivity
import com.example.playlist_maker_2022.presentation.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.search.setOnClickListener {
            val intent = Intent(this, SearchingActivity::class.java)
            startActivity(intent)
        }
        binding.media.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }
        binding.setting.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}