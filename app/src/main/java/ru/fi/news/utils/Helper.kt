package ru.fi.news.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat


fun Context.isInternetAvailable () : Boolean{
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager?.activeNetworkInfo != null
}