package com.videolayer.sdk.core

import android.content.Context
import android.view.View
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.event.LayerEvent

/**
 * 视频图层抽象基类
 * 所有自定义图层需继承此类
 */
abstract class VideoLayer {
    open val priority: Int = 0
    var isVisible: Boolean = true
        set(value) {
            field = value
            onVisibilityChanged(value)
        }

    var host: VideoLayerHost? = null
        internal set

    var currentVideoItem: VideoItem? = null
        internal set

    // 创建图层视图
    abstract fun onCreateView(context: Context): View

    // 绑定视频数据
    open fun onBindData(videoItem: VideoItem) {
        currentVideoItem = videoItem
    }

    // 事件分发
    open fun onReceiveEvent(event: LayerEvent): Boolean = false

    // 生命周期
    open fun onAttachedToHost() {}
    open fun onDetachedFromHost() {}
    open fun onVisibilityChanged(visible: Boolean) {}
    open fun onDestroy() {}

    // 向宿主发送事件
    protected fun sendEvent(event: LayerEvent) {
        host?.dispatchEvent(event)
    }
}