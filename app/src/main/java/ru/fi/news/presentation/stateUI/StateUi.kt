package ru.fi.news.presentation.stateUI

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.fi.news.domain.News

data class StateUi(
    val news : Flow<PagingData<News>> = MutableStateFlow(PagingData.empty()),
    val errorIsGot : Boolean = false,
    val isShowWebView : Boolean = false,
    val isCanRefresh : Boolean = false,
    val url : String = "",
    val noMoreNews : Boolean = false
)
