package ru.fi.news.presentation

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import org.koin.androidx.compose.koinViewModel
import ru.fi.news.R
import ru.fi.news.domain.News
import ru.fi.news.presentation.event.UIevent
import ru.fi.news.presentation.stateUI.StateUi
import ru.fi.news.viewModel.NewsViewModel

@Composable
fun NewsScreen(newsViewModel : NewsViewModel = koinViewModel()){

    val stateUi = newsViewModel.stateUi
    val context = LocalContext.current
    val news = stateUi.news.collectAsLazyPagingItems()

    LaunchedEffect(news.loadState){
        newsViewModel.onEvent(UIevent.CheckLoadState(news))

        if(newsViewModel.stateUi.errorIsGot){
            newsViewModel.onEvent(UIevent.CanRefresh)
        }else{
            if(stateUi.isCanRefresh) newsViewModel.onEvent(UIevent.CanNotRefresh)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        if(news.loadState.refresh is LoadState.Loading){
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }else{
            NewsList(
                news = news,
                stateUi = stateUi,
                onClick =  { selectedNews ->
                    newsViewModel.onEvent(UIevent.ShowWebView(selectedNews.url))
                },
                onRefresh = {
                    newsViewModel.onEvent(UIevent.RefreshNews(news))
                },
                onEndList = {
                    newsViewModel.onEvent(UIevent.CheckEndListNews(news))
                }
            )
        }
    }

    AnimatedVisibility(
        visible = stateUi.isShowWebView,
        enter = slideInHorizontally(animationSpec = tween(500, 200)),
        exit = slideOutHorizontally(targetOffsetX = {-it * 2 / 2})
    ) {
        WebViewNews(url = stateUi.url) {
            newsViewModel.onEvent(UIevent.HideWebView)
            newsViewModel.onEvent(UIevent.CheckInternet(context))
        }
    }
}

@Composable
fun NewsList(
    news: LazyPagingItems<News>,
    stateUi: StateUi,
    onEndList : () -> Unit,
    onRefresh : () -> Unit,
    onClick: (News) -> Unit
){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
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
        item{
            onEndList()
            Column(modifier = Modifier.fillMaxWidth()) {
                if(stateUi.noMoreNews){
                    Text(
                        text = stringResource(R.string.noMoreNews),
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }else if(stateUi.isCanRefresh){
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = stringResource(R.string.messageInternetNotConnection))
                            Button(onClick = {
                                onRefresh()
                            }) {

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = stringResource(R.string.Refresh))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "")
                                }
                            }
                        }

                    }
                }else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
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

    AndroidView(factory = { contextView ->
        WebView(contextView).apply {
            webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    //isLoading = false
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
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

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }

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

@Composable
fun NewsItem(
    news : News,
    onClick : (News) -> Unit
){
    var isError : Boolean by rememberSaveable {
        mutableStateOf(false)
    }

    var isImageLoading : Boolean by rememberSaveable {
        mutableStateOf(true)
    }

    var expandedImage by rememberSaveable { mutableStateOf(false) }

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
            isError = true
        }
    )

    var maxHeightImage by remember{
        mutableStateOf(if(painter.state is AsyncImagePainter.State.Success) painter.intrinsicSize.height.dp else 300.dp)
    }

    LaunchedEffect(painter.state){
        maxHeightImage = if(painter.state is AsyncImagePainter.State.Success) painter.intrinsicSize.height.dp else 300.dp
    }

    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier
            .padding(top = 20.dp)
            .clickable {
                onClick(news)
            }
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column{
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .heightIn(max = if (expandedImage) maxHeightImage else 300.dp),
                contentAlignment = Alignment.BottomStart
            ){
                Image(
                    painter = painter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(isImageLoading){
                        CircularProgressIndicator(
                            color = Color.Black.copy(0.5f),
                        )
                    }else if (isError){
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                        )
                    }

                    Text(
                        text = news.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                            .align(Alignment.Start)
                    )
                    if(
                        painter.state is AsyncImagePainter.State.Success
                        && (painter.state as AsyncImagePainter.State.Success).painter.intrinsicSize.height.dp > 1000.dp
                    ){
                        Button(onClick = {
                            expandedImage = !expandedImage
                        },
                            modifier = Modifier
                                .padding(8.dp)

                        ) {
                            if(expandedImage)
                                Icon(imageVector = Icons.Outlined.KeyboardArrowUp, "")
                            else
                                Icon(imageVector = Icons.Outlined.KeyboardArrowDown, "")
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = news.description)

                Divider(modifier = Modifier.padding(5.dp))

                Text(text = news.publishedAt)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsItemPreview(){
//    NewsItem(
//        news = News(
//            "",
//            "",
//            "Bla Bla Bla Bla Bla Bla Bla",
//            "22 22 22",
//            "Bla",
//            "Bla! Bla! Bla!",
//            "g",
//            ""
//        ),
//        image = R.drawable.cat_background,
//        onClick = {}
//    )
}


