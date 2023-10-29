package ru.fi.news.presentation.event

import android.content.Context
import androidx.paging.compose.LazyPagingItems
import ru.fi.news.domain.News

sealed class UIevent(){
    data class ShowWebView(val url : String) : UIevent()
    data class RefreshNews(val news : LazyPagingItems<News>) : UIevent()
    data class CheckInternet(val context : Context) : UIevent()
    object CanRefresh : UIevent()
    object HideWebView : UIevent()
    object  NotCanRefresh: UIevent()

}
