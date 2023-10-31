package ru.fi.news.viewModel

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.fi.news.R
import ru.fi.news.data.remote.NewsRepository
import ru.fi.news.presentation.event.UIevent
import ru.fi.news.presentation.stateUI.StateUi
import ru.fi.news.utils.isInternetAvailable

class NewsViewModel(newsRepository : NewsRepository) : ViewModel() {

    var stateUi by mutableStateOf(StateUi())

    init {
        viewModelScope.launch {
            val news = newsRepository
                .getNews()
                .cachedIn(viewModelScope)

            stateUi = stateUi.copy(
                news = news
            )
        }
    }

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
                event.news.retry()
                stateUi
            }
            UIevent.CanRefresh ->{
                stateUi.copy(
                    isCanRefresh = true
                )
            }
            UIevent.CanNotRefresh -> {
                stateUi.copy(
                    isCanRefresh = false
                )
            }
            is UIevent.CheckInternet -> {
                if(!event.context.isInternetAvailable()){
                    Toast.makeText(
                        event.context,
                        R.string.InternetConnectionIsNotExist,
                        Toast.LENGTH_SHORT)
                        .show()
                    stateUi.copy(
                        isCanRefresh = true
                    )
                }else{
                    stateUi
                }
            }
            is UIevent.CheckLoadState -> {
                val isError = event.news.loadState.append is LoadState.Error
                        && ((event.news.loadState.append as? LoadState.Error)?.error as? HttpException)?.code() != 426
                        && ((event.news.loadState.append as? LoadState.Error)?.error as? HttpException)?.code() != 429
                        && ((event.news.loadState.refresh as? LoadState.Error)?.error as? HttpException)?.code() != 429
                        || event.news.loadState.refresh is LoadState.Error

                stateUi.copy(
                    errorIsGot = isError
                )
            }
            is UIevent.CheckEndListNews -> {
                val newsIsNoMore = ((event.news.loadState.append as? LoadState.Error)?.error as? HttpException)?.code() == 426
                        || ((event.news.loadState.append as? LoadState.Error)?.error as? HttpException)?.code() == 429
                        || ((event.news.loadState.refresh as? LoadState.Error)?.error as? HttpException)?.code() == 429
                        || event.news.loadState.refresh.endOfPaginationReached
                        || event.news.loadState.append.endOfPaginationReached

                stateUi.copy(
                    noMoreNews = newsIsNoMore
                )
            }
        }
    }
}