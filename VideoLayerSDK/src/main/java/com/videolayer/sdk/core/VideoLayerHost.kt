package com.videolayer.sdk.core

import android.content.Context
import android.widget.FrameLayout
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.event.LayerEvent

/**
 * 管理多个图层的容器
 */
class VideoLayerHost(context: Context) : FrameLayout(context) {

    public val layers = mutableListOf<VideoLayer>()
    private val layerViews = mutableMapOf<VideoLayer, android.view.View>()

    fun addLayer(layer: VideoLayer) {
        if (layers.contains(layer)) return

        layers.add(layer)
        layers.sortByDescending { it.priority }
        layer.host = this
        val layerView = layer.onCreateView(context)
        layerViews[layer] = layerView
        addView(layerView)
        layer.onAttachedToHost()
    }

    fun removeLayer(layer: VideoLayer) {
        if (!layers.contains(layer)) return

        layers.remove(layer)
        layerViews[layer]?.let { view ->
            removeView(view)
            layerViews.remove(layer)
        }
        layer.onDetachedFromHost()
        layer.host = null
    }

    fun bindData(videoItem: VideoItem) {
        layers.forEach { it.onBindData(videoItem) }
    }

    fun dispatchEvent(event: LayerEvent) {
        for (layer in layers) {
            if (layer.isVisible && layer.onReceiveEvent(event)) break
        }
    }

    fun destroy() {
        layers.forEach { it.onDestroy() }
        layers.clear()
        layerViews.clear()
        removeAllViews()
    }

    inline fun <reified T : VideoLayer> getLayer(): T? {
        return layers.find { it is T } as? T
    }
}