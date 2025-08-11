package com.videolayer.sdk.event

sealed class LayerEvent {
    data class Like(val videoId: String) : LayerEvent()
    data class Share(val videoId: String) : LayerEvent()
    data class Comment(val videoId: String) : LayerEvent()
    data class SingleTap(val videoId: String) : LayerEvent()
    data class DoubleTap(val videoId: String) : LayerEvent()
    data class LongPress(val videoId: String) : LayerEvent()
    data class Scroll(val videoId: String, val params: Map<String, Any>) : LayerEvent()
    data class Fling(val videoId: String, val velocityX: Float, val velocityY: Float) : LayerEvent()
    data class Down(val videoId: String) : LayerEvent()
    data class ShowPress(val videoId: String) : LayerEvent()
    data class Custom(val name: String, val payload: Any? = null) : LayerEvent()
}