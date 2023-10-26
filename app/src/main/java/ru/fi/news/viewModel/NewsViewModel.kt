package ru.fi.news.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.map
import ru.fi.news.data.toNews
import ru.fi.news.data.local.NewsEntity

class NewsViewModel(
    pager : Pager<Int, NewsEntity>
) : ViewModel() {

    val newsPagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toNews() }
        }
        .cachedIn(viewModelScope)
}