package ru.fi.news.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.fi.news.data.remote.NewsApi
import ru.fi.news.data.remote.NewsRemoteMediator
import ru.fi.news.data.local.NewsDatabase
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.viewModel.NewsViewModel

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
        Retrofit.Builder()
            .baseUrl(NewsApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    single<Pager<Int, NewsEntity>> {

        val newsDb : NewsDatabase = get()
        val newsApi : NewsApi = get()

        Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 5
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

    viewModelOf(::NewsViewModel)
}