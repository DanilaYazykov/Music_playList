package com.example.playlist_maker_2022.checkings

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.playlist_maker_2022.R

@Suppress("DEPRECATION")
class CheckingInternet {

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }


    object DialogManager {
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
}