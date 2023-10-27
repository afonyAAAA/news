package ru.fi.news.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.delay
import retrofit2.HttpException
import ru.fi.news.data.local.NewsDatabase
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.data.toNewsEntity
import java.io.IOException
import kotlin.math.ceil

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val newsDb : NewsDatabase,
    private val newsApi: NewsApi
) : RemoteMediator<Int, NewsEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>,
    ): MediatorResult {
        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH -> {
                    5
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
                LoadType.APPEND -> {
                    val isCanLoadNews = if(state.anchorPosition != null){
                        state.anchorPosition!! >= state.pages.size - state.config.prefetchDistance
                    } else {
                        false
                    }
                    val lastItem = state.lastItemOrNull()
                    return if(lastItem == null){
                        MediatorResult.Success(endOfPaginationReached = false)
                    }else{
                        if(isCanLoadNews){
                            val page = lastItem?.id.let {(it!!.toDouble() / state.config.pageSize).toInt() + 1}
                            val prefetchedNews = newsApi.getNews(page = page, pageSize = state.config.pageSize)
                            val prefetchedEntities = prefetchedNews.articles.map { it.toNewsEntity() }
                            newsDb.dao.upsertAll(prefetchedEntities)
                            MediatorResult.Success(endOfPaginationReached = prefetchedEntities.isEmpty())
                        }
                        MediatorResult.Success(false)
                    }
                }
            }

            if(state.pages.isEmpty()){
                val news = newsApi.getNews(
                    page = loadKey,
                    pageSize = state.config.pageSize
                )
                val newsEntities = news.articles.map { it.toNewsEntity() }
                newsDb.dao.upsertAll(newsEntities)
            }

            if(state.pages.isNotEmpty()){
                if(loadType == LoadType.REFRESH){
                    newsDb.dao.deleteAllNews()
                }
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        }catch (e : IOException){
            MediatorResult.Error(e)
        }catch (e : HttpException){
            MediatorResult.Error(e)
        }
    }
}