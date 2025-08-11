package com.videolayer.sdk.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.event.LayerEvent

/**
 * 视频图层抽象基类 (Compose版)
 * 所有自定义图层需继承此类
 */
abstract class VideoLayer {
    open val priority: Int = 0
    private val _currentVideoItem = mutableStateOf<VideoItem?>(null)
    var currentVideoItem: VideoItem?
        get() = _currentVideoItem.value
        internal set(value) {
            _currentVideoItem.value = value
        }
    private var _isVisible = mutableStateOf(true)
    var isVisible: Boolean
        get() = _isVisible.value
        set(value) {
            _isVisible.value = value
            onVisibilityChanged(value)
        }

    var host: VideoLayerHost? = null
        internal set

    // Composable UI
    @Composable
    abstract fun Render()

    // 绑定视频数据
    open fun onBindData(videoItem: VideoItem) {
        currentVideoItem = videoItem
    }

    open fun onVisibilityChanged(visible: Boolean) {
        // 可选实现：当图层可见性变化时的处理逻辑
    }

    open fun onAttachedToHost() {}
    open fun onDetachedFromHost() {}
    open fun onDestroy() {}

    // 事件分发
    open fun onReceiveEvent(event: LayerEvent): Boolean = false

    // 向宿主发送事件
    protected fun sendEvent(event: LayerEvent) {
        host?.dispatchEvent(event)
    }


    // ========================
    fun getVideoId(): String {
        return currentVideoItem?.id ?: throw NullPointerException("currentVideoItem id is null")
    }
}