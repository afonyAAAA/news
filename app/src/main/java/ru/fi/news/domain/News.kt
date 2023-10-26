package ru.fi.news.domain

data class News(
    val author: String?,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: String,
    val title: String,
    val url: String,
    val urlToImage: String?
)
