package ru.fi.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {
    @GET("everything?q=ios&from=2019-04-00&sortBy=publishedAt&apiKey=$API_KEY")
    suspend fun getNews(
        @Query("page") page : Int
    ) : NewsDto

    companion object {
        const val API_KEY = "26eddb253e7840f988aec61f2ece2907"
        const val BASE_URL = "https://newsapi.org/v2/"
    }
}