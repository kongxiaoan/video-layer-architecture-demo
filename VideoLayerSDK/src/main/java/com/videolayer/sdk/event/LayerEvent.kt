package com.videolayer.sdk.event

/**
 * 图层间事件传递结构（密封类设计，类型安全，易扩展）
 */
sealed class LayerEvent {
    // 点赞
    data class Like(val videoId: String) : LayerEvent()

    // 分享
    data class Share(val videoId: String) : LayerEvent()

    // 单击
    data class SingleTap(val videoId: String) : LayerEvent()

    // 双击
    data class DoubleTap(val videoId: String) : LayerEvent()

    // 长按
    data class LongPress(val videoId: String) : LayerEvent()

    // 滑动
    data class Scroll(val videoId: String,val params: Map<String, Any?>) : LayerEvent()

    // 轻扫
    data class Fling(val videoId: String, val params: Map<String, Any?>) : LayerEvent()

    // 按下
    data class Down(val videoId: String) : LayerEvent()

    // 显示按压（手指触摸但未松开）
    data class ShowPress(val videoId: String) : LayerEvent()

    // 其它扩展事件
    data class Custom(val name: String, val payload: Any? = null) : LayerEvent()
}