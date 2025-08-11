package com.kpa.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.kpa.example.ui.theme.VideolayerarchitecturedemoTheme
import com.videolayer.sdk.core.VideoView
import com.videolayer.sdk.data.VideoItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideolayerarchitecturedemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoListScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun VideoListScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // 示例数据
    val videos = remember {
        listOf(
            VideoItem("1", "演示视频一", "https://via.placeholder.com/350x600.png?text=Cover1", ""),
            VideoItem("2", "演示视频二", "https://via.placeholder.com/350x600.png?text=Cover2", ""),
            VideoItem("3", "演示视频三", "https://via.placeholder.com/350x600.png?text=Cover3", "")
        )
    }
    // 使用 AndroidView 嵌入 SDK 的 VideoView
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            VideoView(ctx).apply {
                bindData(videos)
            }
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VideolayerarchitecturedemoTheme {
        Greeting("Android")
    }
}