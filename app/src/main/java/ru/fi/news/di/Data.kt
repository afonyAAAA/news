package ru.fi.news.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.fi.news.data.remote.NewsApi
import ru.fi.news.data.remote.NewsRemoteMediator
import ru.fi.news.data.local.NewsDatabase
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.data.remote.NewsImpl
import ru.fi.news.data.remote.NewsRepository
import ru.fi.news.presentation.NewsViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
val dataModule = module {

    single<NewsDatabase> {
        Room.databaseBuilder(
            context = get(),
            NewsDatabase::class.java,
            "news_database"
        ).build()
    }

    single<NewsApi> {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(NewsApi.BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    single<Pager<Int, NewsEntity>> {

        val newsDb : NewsDatabase = get()
        val newsApi : NewsApi = get()

        Pager(
            config = PagingConfig(
                pageSize = 100,
                prefetchDistance = 5,
                initialLoadSize = 100
            ),
            remoteMediator = NewsRemoteMediator(
                newsDb = newsDb,
                newsApi = newsApi
            ),
            pagingSourceFactory = {
                newsDb.dao.pagingSource()
            }
        )
    }

    single<NewsRepository>{
        NewsImpl(pager = get())
    }

    viewModelOf(::NewsViewModel)
}
