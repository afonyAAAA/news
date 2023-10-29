package ru.fi.news

import android.icu.number.Scale
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.fi.news.presentation.NewsScreen
import ru.fi.news.ui.theme.NewsTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            NewsTheme{
                Scaffold { paddingValues ->
                    val scale = remember {
                        Animatable(0f)
                    }

                    var hideSplashScreen by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(key1 = true) {
                        scale.animateTo(
                            targetValue = 0.7f,
                            animationSpec = tween(
                                durationMillis = 800,
                                easing = {
                                    OvershootInterpolator(4f).getInterpolation(it)
                                })
                        )
                        delay(3000L)
                        hideSplashScreen = true
                    }

                    if(hideSplashScreen){
                        Box(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                        ){
                            NewsScreen()
                        }
                    }else{
                        SplashScreen(scale = scale)
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(scale : Animatable<Float, AnimationVector1D>){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.cat_background),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .heightIn(min = 75.dp, max = 200.dp)
                .widthIn(min = 100.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .scale(scale = scale.value),
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Image(painter = painterResource(id = R.drawable.cat), contentDescription = "")
        }
    }
}