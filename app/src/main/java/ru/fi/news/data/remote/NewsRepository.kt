package ru.fi.news.data.remote

import androidx.paging.Pager
import androidx.paging.PagingData
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.domain.News
import java.util.concurrent.Flow

interface NewsRepository {
    fun getNews() : kotlinx.coroutines.flow.Flow<PagingData<News>>
}