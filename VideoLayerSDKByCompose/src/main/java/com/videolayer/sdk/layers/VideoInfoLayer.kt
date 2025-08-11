package com.videolayer.sdk.layers

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.videolayer.sdk.core.VideoLayer
import com.videolayer.sdk.data.VideoItem

class VideoInfoLayer : VideoLayer() {
    @Composable
    override fun Render() {
        Log.d("VideoInfoLayer", "Render called for VideoInfoLayer ${currentVideoItem}")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, bottom = 48.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "@${currentVideoItem?.title}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentVideoItem?.description ?: "视频描述内容",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }

    override fun onBindData(videoItem: VideoItem) {
        super.onBindData(videoItem)
        Log.d("VideoInfoLayer", "onBindData: ${videoItem.title}")
    }
}