package com.videolayer.sdk.config

/**
 * 缓冲策略配置
 */
data class BufferConfig(
    /**
     * 最小缓冲时长（毫秒）
     */
    val minBufferMs: Int = 15000,

    /**
     * 最大缓冲时长（毫秒）
     */
    val maxBufferMs: Int = 50000,

    /**
     * 播放缓冲时长（毫秒）
     */
    val bufferForPlaybackMs: Int = 2500,

    /**
     * 重新播放缓冲时长（毫秒）
     */
    val bufferForPlaybackAfterRebufferMs: Int = 5000,

    /**
     * 优先选择质量而非流畅性
     */
    val prioritizeTimeOverSizeThresholds: Boolean = false
) {
    companion object {
        /**
         * 默认缓冲配置
         */
        val DEFAULT = BufferConfig()

        /**
         * 高性能缓冲配置
         */
        val HIGH_PERFORMANCE = BufferConfig(
            minBufferMs = 30000,
            maxBufferMs = 100000,
            bufferForPlaybackMs = 5000,
            bufferForPlaybackAfterRebufferMs = 10000,
            prioritizeTimeOverSizeThresholds = true
        )

        /**
         * 省电模式缓冲配置
         */
        val POWER_SAVING = BufferConfig(
            minBufferMs = 5000,
            maxBufferMs = 20000,
            bufferForPlaybackMs = 1000,
            bufferForPlaybackAfterRebufferMs = 2000,
            prioritizeTimeOverSizeThresholds = false
        )

        /**
         * 低延迟配置
         */
        val LOW_LATENCY = BufferConfig(
            minBufferMs = 2000,
            maxBufferMs = 10000,
            bufferForPlaybackMs = 500,
            bufferForPlaybackAfterRebufferMs = 1000,
            prioritizeTimeOverSizeThresholds = false
        )
    }
}