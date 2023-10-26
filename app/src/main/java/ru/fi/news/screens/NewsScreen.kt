package ru.fi.news.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import ru.fi.news.domain.News

@Composable
fun NewsScreen(news : LazyPagingItems<News>){

    val context = LocalContext.current

    LaunchedEffect(news.loadState){
        if(news.loadState.refresh is LoadState.Error){
            Toast.makeText(
                context,
                "Error: " + (news.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        if(news.loadState.refresh is LoadState.Loading){
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        NewsList(news = news)
    }

}


@Composable
fun NewsList(news: LazyPagingItems<News>){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item{
            if(news.loadState.refresh is LoadState.Loading){

            }
        }
        items(news){ news ->
            if(news != null){
                NewsItem(
                    news = news,
                    onClick = {

                    }
                )
            }
        }
    }
}

@Composable
fun NewsItem(
    news : News,
    onClick : (News) -> Unit
){

    var isErrorLoading : Boolean by rememberSaveable {
        mutableStateOf(false)
    }

    var isImageLoading : Boolean by rememberSaveable {
        mutableStateOf(true)
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(news.urlToImage)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build(),
        onSuccess = {
            isImageLoading = false
        },
        onError = {
            isErrorLoading = true
        }
    )
    
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier
            .padding(top = 20.dp)
            .clickable {
                onClick(news)
            }
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column{
            Box(
                modifier = Modifier.background(color = Color.Black.copy(0.2f)),
                contentAlignment = Alignment.BottomStart
            ){
                Column {
                    if(isImageLoading){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator(
                                color = Color.Black.copy(0.5f)
                            )
                        }
                    }else if(isErrorLoading){
                        Icon(imageVector = Icons.Outlined.Warning, contentDescription = "")
                    }else{
                        Image(
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxWidth(),
                            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken)
                        )
                    }
                }

                Text(
                    text = news.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                )
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = news.description)

                Text(text = news.publishedAt)
            }

        }
    }
}