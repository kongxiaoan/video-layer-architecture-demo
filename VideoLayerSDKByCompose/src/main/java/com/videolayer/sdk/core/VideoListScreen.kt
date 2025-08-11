package com.videolayer.sdk.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.layers.VideoButtonLayer
import com.videolayer.sdk.layers.VideoGestureLayer
import com.videolayer.sdk.layers.VideoInfoLayer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoListScreen(
    videos: List<VideoItem>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { videos.size })

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { pageIndex ->
        val video = videos[pageIndex]

        val host = remember {
            VideoLayerHost().apply {
                addLayer(VideoGestureLayer())
                addLayer(VideoInfoLayer()) // 默认添加一个信息图层
                addLayer(VideoButtonLayer())
            }
        }
        LaunchedEffect(video) {
            host.bindData(video)
        }
        VideoLayerHostView(host, modifier = Modifier.fillMaxSize())
    }
}