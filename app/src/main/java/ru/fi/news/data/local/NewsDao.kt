package ru.fi.news.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NewsDao {
    @Upsert
    suspend fun upsertAll(news : List<NewsEntity>)

    @Query("SELECT * FROM newsentity")
    fun pagingSource() : PagingSource<Int, NewsEntity>
}