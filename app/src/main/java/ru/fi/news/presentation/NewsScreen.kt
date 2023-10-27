package ru.fi.news.presentation

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import org.koin.androidx.compose.koinViewModel
import ru.fi.news.domain.News
import ru.fi.news.presentation.UIevent.UIevent
import ru.fi.news.viewModel.NewsViewModel

@Composable
fun NewsScreen(){
    val newsViewModel : NewsViewModel = koinViewModel()
    val news = newsViewModel.newsPagingFlow.collectAsLazyPagingItems()
//    val context = LocalContext.current
//    val stateUi = newsViewModel.stateUi

//    LaunchedEffect(news.loadState){
//        if(!newsViewModel.isInternetAvailable(context)){
//
//        }
//        if(news.loadState.refresh is LoadState.Error){
//            Toast.makeText(
//                context,
//                "Error: " + (news.loadState.refresh as LoadState.Error).error.message,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }

    //AnimatedVisibility(visible = !stateUi.isShowWebView) {
        Box(modifier = Modifier.fillMaxSize()){
            if(news.loadState.refresh is LoadState.Loading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }else{
                NewsList(news = news,
                    onClick =  { selectedNews ->
                        newsViewModel.onEvent(UIevent.ShowWebView(selectedNews.url))
                    },
                    onRefresh = {
                        newsViewModel.onEvent(UIevent.RefreshNews(news))
                    }
                )
            }
        }
    //}
    //AnimatedVisibility(visible = stateUi.isShowWebView) {
//        WebViewNews(url = stateUi.url) {
//            newsViewModel.onEvent(UIevent.HideWebView)
//        }
   // }
}


@Composable
fun NewsList(news: LazyPagingItems<News>, onClick: (News) -> Unit, onRefresh : () -> Unit){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item{
            Spacer(modifier = Modifier.height(10.dp))
            if(news.loadState.refresh is LoadState.Error){
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "Интернет пропал!")
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = {
                            onRefresh()
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Перезагрузить")
                                Spacer(modifier = Modifier.height(10.dp))
                                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "")
                            }
                        }
                    }
                }
            }
        }
        items(news.itemCount){ index ->
            if(news[index] != null){
                NewsItem(
                    news = news[index]!!,
                    onClick = {
                        onClick(news[index]!!)
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
                Column(Modifier.fillMaxSize()) {
                    if(isImageLoading){
                        CircularProgressIndicator(
                            color = Color.Black.copy(0.5f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }else if(isErrorLoading){
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.CenterHorizontally)
                        )
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
                Divider(
                    modifier = Modifier.padding(5.dp)
                )
                Text(text = news.publishedAt)
            }
        }
    }
}
@Composable
fun WebViewNews(
    url : String,
    onBack: () -> Unit
){
    val context = LocalContext.current

    var isLoading by rememberSaveable { mutableStateOf(true) }

    var webView by remember{
        mutableStateOf(WebView(context))
    }
    val webViewState by rememberSaveable{
        mutableStateOf(Bundle())
    }

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }

    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoading = false
                }
            }

            if(!webViewState.isEmpty){
                restoreState(webViewState)
                webView = this
            }else{
                webView = this
                loadUrl(url)
            }
        }},
        update = {
            webView.restoreState(webViewState)
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ){
        Button(onClick = {
            onBack()
        },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "")
        }
    }

    DisposableEffect(Unit){
        onDispose {
            webView.saveState(webViewState)
        }
    }

    BackHandler {
        if(webView.canGoBack()) webView.goBack() else onBack()
    }
}