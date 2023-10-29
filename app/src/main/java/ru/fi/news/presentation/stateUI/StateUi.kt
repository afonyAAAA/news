package ru.fi.news.presentation.stateUI

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.fi.news.domain.News

data class StateUi(
    val news : Flow<PagingData<News>>,
    val isShowWebView : Boolean = false,
    val isCanRefresh : Boolean = false,
    val url : String = ""
)
