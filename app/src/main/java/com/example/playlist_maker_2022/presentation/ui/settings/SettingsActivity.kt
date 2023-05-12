package com.example.playlist_maker_2022.presentation.ui.settings

import android.content.Intent
import android.net.Uri
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
            shareApplication()
        }

        binding.btUpdate.setOnClickListener {
            sendEmail()
        }

        binding.goToAgreement.setOnClickListener {
            goToAgreement()
        }
    }

    private fun shareApplication() {
        val shareApplication = Intent(Intent.ACTION_SEND)
        shareApplication.type = "text/plain"
        shareApplication.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicumLink))
        startActivity(shareApplication)
    }

    private fun sendEmail() {
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse(getString(R.string.mailTo))
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message))
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messageTheme))
        startActivity(sendIntent)
    }

    private fun goToAgreement() {
        val url = Uri.parse(getString(R.string.practicumOffer))
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }
}