package com.example.playlist_maker_2022.presentation.ui.media.createNewPlaylist

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker_2022.R
import com.example.playlist_maker_2022.databinding.FragmentNewPlaylistBinding
import com.example.playlist_maker_2022.domain.models.Playlists
import com.example.playlist_maker_2022.presentation.presenters.media.createNewPlaylist.NewPlaylistViewModel
import com.example.playlist_maker_2022.presentation.util.bindingFragment.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {

    private val viewModel by viewModel<NewPlaylistViewModel>()

    private val requester = PermissionRequester.instance()
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var textTitle = ""
    private var playlistImage: File? = null
    private var textDescription = ""
    private var uriLink: Uri = Uri.EMPTY
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyBoardBehavior()
        workingWithText()
        binding.ivAddPicture.setOnClickListener { requestPermissionAndPickImage() }
        getImageUri()
        binding.backFromNewPlaylist.setOnClickListener { exitBehavior() }
        backPressedCallback()
    }

    private fun keyBoardBehavior() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun workingWithText() {
        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            if (text?.length!! > 0) {
                renderBoxStrokeEditTextColor(binding.tvName, text)
                textTitle = text.toString()
                binding.buttonCreate.setBackgroundResource(R.drawable.bt_round_drawable_blue)
                binding.buttonCreate.setOnClickListener {
                    showSuccessToast()
                    viewModel.insertPlaylist(Playlists(
                        playlistId = 0,
                        playlistName = textTitle,
                        playlistDescription = textDescription,
                        playlistImage = if (uriLink != Uri.EMPTY) uriLink.toString() else "",
                    ))
                    findNavController().navigateUp()
                }
            } else {
                textTitle = ""
                renderBoxStrokeEditTextColor(binding.tvName, text)
                binding.buttonCreate.setBackgroundResource(R.drawable.bt_round_drawable)
            }
        }

        binding.playlistDescription.doOnTextChanged { text,_,_,_ ->
            if (text?.length!! > 0) {
                renderBoxStrokeEditTextColor(binding.playlistDescriptionPlaylist, text)
                textDescription =  text.toString()
            } else {
                textDescription = ""
                renderBoxStrokeEditTextColor(binding.playlistDescriptionPlaylist, text)
            }
        }
    }

    private fun getImageUri() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(binding.imageBox)
                saveImageToPrivateStorage(uri)
                binding.ivAddPicture.visibility = View.GONE
            } else {
                uriLink = Uri.EMPTY
                binding.ivAddPicture.visibility = View.VISIBLE
            }
        }
    }

    private fun requestPermissionAndPickImage() {
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requester.request(Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
                    when (result) {
                        is PermissionResult.Granted ->
                            pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        is PermissionResult.Denied.DeniedPermanently -> showFrameDialog()
                        is PermissionResult.Denied.NeedsRationale -> showFrameDialog()
                        is PermissionResult.Cancelled -> return@collect
                    }
                }
            } else {
                pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun renderBoxStrokeEditTextColor(view: TextInputLayout, text: CharSequence?) {
        if (!text.isNullOrEmpty()) {
            view.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(),
                R.color.textinput_box_stroke_color_blue)
            view.setBoxStrokeColorStateList(resources.getColorStateList(R.color.textinput_box_stroke_color_blue,
                requireContext().theme))
        } else {
            view.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(),
                R.color.textinput_box_stroke_color_selector)
            view.setBoxStrokeColorStateList(resources.getColorStateList(R.color.textinput_box_stroke_color_selector,
                requireContext().theme))
        }
    }

    private fun showSuccessToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.playlistCreated, textTitle),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showFrameDialogDeleteOrCancel() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.askDeleteOrCancel))
            .setMessage(getString(R.string.warnDeleteOrCancel))
            .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.toEnd)) { _, _ -> findNavController().navigateUp() }
            .show()
    }

    private fun showFrameDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.dialogTitle))
        builder.setMessage(getString(R.string.askGivePermission))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.fromParts("package", context?.packageName, null)
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        if (uri == Uri.EMPTY) return
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        playlistImage = File(filePath, "cover_$timestamp.jpg")
        uriLink = playlistImage!!.toUri()
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(playlistImage)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun exitBehavior() {
        if (textTitle.isNotEmpty()
            || textDescription.isNotEmpty() || uriLink != Uri.EMPTY) showFrameDialogDeleteOrCancel()
        else findNavController().navigateUp()
    }

    private fun backPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitBehavior()
                }
            })
    }
}