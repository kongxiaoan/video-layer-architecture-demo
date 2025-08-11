package com.videolayer.sdk.data

/**
 * 视频条目数据结构
 */
data class VideoItem(
    val id: String,
    val title: String,
    val coverUrl: String,
    val videoUrl: String,
    val duration: Long = 0L,
    val extra: Map<String, Any>? = null
)