package ru.fi.news.presentation.UIevent

import androidx.paging.compose.LazyPagingItems
import ru.fi.news.domain.News

sealed class UIevent(){
    data class ShowWebView(val url : String) : UIevent()
    data class RefreshNews(val news : LazyPagingItems<News>) : UIevent()
    object HideWebView : UIevent()

}
