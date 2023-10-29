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
            val loadKey = when(loadType) {
                LoadType.REFRESH -> 5
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        5
                    } else {
                        val page = (lastItem.id!! / state.config.pageSize) + 1
                        page
                    }
                }
            }

            val news = newsApi.getNews(
                page = loadKey,
                pageSize = state.config.pageSize
            )

            newsDb.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    newsDb.dao.deleteAllNews()
                }
                val beerEntities = news.articles.map{ it.toNewsEntity() }
                newsDb.dao.upsertAll(beerEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = news.articles.size < state.config.pageSize
            )

        }catch (e : IOException){
            MediatorResult.Error(e)
        }catch (e : HttpException){
            MediatorResult.Error(e)
        }
    }
}