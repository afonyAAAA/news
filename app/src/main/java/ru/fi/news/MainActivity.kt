package ru.fi.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel
import ru.fi.news.screens.NewsList
import ru.fi.news.screens.NewsScreen
import ru.fi.news.ui.theme.NewsTheme
import ru.fi.news.viewModel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{

            val newsViewModel : NewsViewModel = koinViewModel()
            val news = newsViewModel.newsPagingFlow.collectAsLazyPagingItems()

            NewsTheme{
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ){
                        NewsScreen(news = news)
                    }
                }
            }
        }
    }

}