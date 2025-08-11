package com.videolayer.sdk.core

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.event.LayerEvent

class VideoLayerHost {
    val layers = mutableStateListOf<VideoLayer>()
    fun addLayer(layer: VideoLayer) {
        if(layers.contains(layer)) return

        layers.add(layer)
        layers.sortByDescending { it.priority }
        layer.host = this
        layer.onAttachedToHost()
    }

    fun removeLayer(layer: VideoLayer) {
        if(!layers.contains(layer)) return

        layers.remove(layer)
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
    }

    inline fun <reified T : VideoLayer> getLayer(): T? {
        return layers.find { it is T } as? T
    }
}

@Composable
fun VideoLayerHostView(host: VideoLayerHost, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        host.layers.forEach { layer ->
            if(layer.isVisible) {
                Log.d("VideoLayerHostView", "Rendering layer: ${layer::class.java.simpleName}")
                layer.Render()
            }
        }
    }
}

@Composable
fun showToast() {
    val context = LocalContext.current
    Toast.makeText(context, "VideoLayerHost is ready", Toast.LENGTH_SHORT).show()
}