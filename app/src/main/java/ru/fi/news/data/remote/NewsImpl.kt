package ru.fi.news.data.remote

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.data.toNews
import ru.fi.news.domain.News

class NewsImpl(private val pager : Pager<Int, NewsEntity>) : NewsRepository {
    override fun getNews(): Flow<PagingData<News>> {
        return pager.flow.map { pagingData -> pagingData.map { it.toNews() }}
    }

}