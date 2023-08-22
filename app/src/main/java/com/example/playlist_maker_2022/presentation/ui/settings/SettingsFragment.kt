package com.example.playlist_maker_2022.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.FragmentSettingsBinding
import com.example.playlist_maker_2022.presentation.viewModels.settings.SettingsViewModel
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.switchThemeChecked.observe(viewLifecycleOwner) { checked ->
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