package ru.fi.news.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import ru.fi.news.data.toNews
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.presentation.UIevent.UIevent
import ru.fi.news.presentation.stateUI.StateUi

class NewsViewModel(
    pager : Pager<Int, NewsEntity>
) : ViewModel() {

    val newsPagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toNews() }
        }
        .cachedIn(viewModelScope)

    var stateUi by mutableStateOf(StateUi())

    fun onEvent(event : UIevent){
        stateUi = when(event){
            UIevent.HideWebView -> {
                stateUi.copy(
                    isShowWebView = false
                )
            }
            is UIevent.ShowWebView -> {
                stateUi.copy(
                    url = event.url,
                    isShowWebView = true
                )
            }
            is UIevent.RefreshNews -> {
                event.news.refresh()
                stateUi
            }
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }

}