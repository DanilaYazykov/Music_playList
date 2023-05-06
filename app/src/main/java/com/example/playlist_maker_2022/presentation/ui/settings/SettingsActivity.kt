package com.example.playlist_maker_2022.presentation.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.ActivitySettingsBinding
import com.example.playlist_maker_2022.presentation.presenters.settings.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        binding.backFromSetting.setOnClickListener {
            finish()
        }

        viewModel.switchThemeChecked.observe(this) { checked ->
            binding.switchThemeMain.isChecked = checked
        }
        binding.switchThemeMain.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        binding.shareApplication.setOnClickListener {
            viewModel.shareApplication()
        }

        binding.btUpdate.setOnClickListener {
            viewModel.sendEmail()
        }

        binding.goToAgreement.setOnClickListener {
            viewModel.goToAgreement()
        }
    }
}