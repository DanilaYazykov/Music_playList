package com.example.playlist_maker_2022.presentation.presenters.searching

import android.app.AlertDialog
import android.content.Context
import com.example.playlist_maker_2022.R

object CreatorNoInternetDialogManager {
    fun internetSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.NoInternetConnection))
        dialog.setMessage(context.getString(R.string.Connect_to_internet))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.OK)) { _, _ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.Cancel)
        ) { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick(name: String?)
    }
}
