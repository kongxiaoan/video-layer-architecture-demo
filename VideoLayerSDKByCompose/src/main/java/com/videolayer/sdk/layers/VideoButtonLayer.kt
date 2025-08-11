package com.videolayer.sdk.layers

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.videolayer.sdk.R
import com.videolayer.sdk.core.VideoLayer
import com.videolayer.sdk.event.LayerEvent

class VideoButtonLayer : VideoLayer() {
    override val priority: Int
        get() = 20

    @Composable
    override fun Render() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            IconButton(onClick = { sendEvent(LayerEvent.Like(getVideoId())) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_like),
                    contentDescription = "Like",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = { sendEvent(LayerEvent.Comment(getVideoId())) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_comment),
                    contentDescription = "Comment",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = { sendEvent(LayerEvent.Share(getVideoId())) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_share),
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
        }
    }

    override fun onReceiveEvent(event: LayerEvent): Boolean {
        Log.d("VideoButtonLayer", "Received event: $event")
        return super.onReceiveEvent(event)
    }

}