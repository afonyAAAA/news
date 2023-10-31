package ru.fi.news.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val author: String? = null,
    val content: String,
    val description: String?,
    val publishedAt: String?,
    val source: String,
    val title: String?,
    val url: String,
    val urlToImage: String?
)
