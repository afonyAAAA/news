package ru.fi.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("everything?q=ios&from=2019-04-00&sortBy=publishedAt&apiKey=310f1b56ed3e4f60b68464c2b8b5275a")
    suspend fun getNews(
        @Query("page") page : Int,
        @Query("pageSize") pageSize : Int
    ) : NewsDto

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
    }
}