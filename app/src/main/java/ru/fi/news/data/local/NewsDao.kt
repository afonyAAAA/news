package ru.fi.news.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NewsDao {
    @Upsert
    suspend fun upsertAll(news : List<NewsEntity>)

    @Query("SELECT * FROM news")
    fun pagingSource() : PagingSource<Int, NewsEntity>

    @Query("UPDATE sqlite_sequence SET seq = 1 where name = 'news'")
    suspend fun updateCounterPrimaryKey()

    @Query("DELETE FROM news")
    suspend fun deleteAllNews()
}