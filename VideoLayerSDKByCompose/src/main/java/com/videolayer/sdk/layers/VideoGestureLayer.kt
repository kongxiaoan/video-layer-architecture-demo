package com.videolayer.sdk.layers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.videolayer.sdk.core.VideoLayer
import com.videolayer.sdk.event.LayerEvent

class VideoGestureLayer : VideoLayer() {
    override val priority: Int
        get() = 100

    @Composable
    override fun Render() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            // 处理点击事件
                            sendEvent(LayerEvent.SingleTap(getVideoId()))
                        },
                        onDoubleTap = { offset ->
                            // 处理双击事件
                            sendEvent(LayerEvent.DoubleTap(getVideoId()))
                        },
                        onLongPress = { offset ->
                            // 处理长按事件
                            sendEvent(LayerEvent.LongPress(getVideoId()))
                        }
                    )
                }
        )
    }
}