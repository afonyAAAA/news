package ru.fi.news.data

import ru.fi.news.data.local.NewsEntity
import ru.fi.news.data.remote.Article
import ru.fi.news.domain.News
import java.text.SimpleDateFormat

fun String.convertToMyDateFormat() : String{
    val inputDateString = this
    val inputFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    val outputFormat = "dd/MM/yyyy"

    val inputFormatter = SimpleDateFormat(inputFormat)
    val outputFormatter = SimpleDateFormat(outputFormat)

    val date = inputFormatter.parse(inputDateString)
    return outputFormatter.format(date)
}

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
        title = title ?: "Без названия",
        description = description ?: "Без описания",
        content = content,
        source = source,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt?.convertToMyDateFormat() ?: "Время публикации - неизвестно"
    )
}

