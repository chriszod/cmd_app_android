package com.cmd.cmd_app_android.view.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import thecmdteam.cmd_app_android.R

fun TextInputEditText.onChange(action: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            action(editable.toString())
        }
    })
}

fun Application.checkConnectivity(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            return when (type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                ConnectivityManager.TYPE_MOBILE -> true
                else -> false
            }
        }
    }
    return false
}

fun TextView.handleError(error: String, valid: Boolean) {
    if (valid) {
        this.text = ""
    } else {
        this.text = error
    }
}

fun makeAlertDialog(context: Context, error: String): AlertDialog.Builder {
    return AlertDialog.Builder(context)
        .setMessage(error)
        .setIcon(R.drawable.ic_error)
        .setPositiveButton("Ok"
        ) { dialog, p1 -> dialog?.cancel() }
}

fun Fragment.navigateActivity(context: Context, activity: Activity) {
    Intent(context, activity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }.also {
        context.startActivity(it)
    }
}