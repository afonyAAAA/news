package ru.fi.news.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ru.fi.news.data.remote.NewsRepository
import ru.fi.news.presentation.event.UIevent
import ru.fi.news.presentation.stateUI.StateUi
import ru.fi.news.utils.isInternetAvailable

class NewsViewModel(newsRepository : NewsRepository) : ViewModel() {

    var stateUi by mutableStateOf(StateUi(
        news = newsRepository
            .getNews()
            .cachedIn(viewModelScope)
    ))

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
            UIevent.CanRefresh ->{
                stateUi.copy(
                    isCanRefresh = true
                )
            }
            UIevent.NotCanRefresh -> {
                stateUi.copy(
                    isCanRefresh = false
                )
            }
            is UIevent.CheckInternet -> {
                if(!event.context.isInternetAvailable()){
                    Toast.makeText(event.context, "Интернет соедениение потеряно", Toast.LENGTH_SHORT).show()
                    stateUi.copy(
                        isCanRefresh = true
                    )
                }else{
                    stateUi
                }
            }
        }
    }
}