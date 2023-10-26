package ru.fi.news.data

import ru.fi.news.domain.News
import ru.fi.news.data.local.NewsEntity
import ru.fi.news.data.remote.Article

fun Article.toNewsEntity() : NewsEntity {
    return NewsEntity(
        author = author,
        title = title,
        description = description,
        content = content,
        source = source.name,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt
    )
}

fun NewsEntity.toNews() : News {
    return News(
        author = author,
        title = title,
        description = description,
        content = content,
        source = source,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt
    )
}


