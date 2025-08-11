package com.videolayer.sdk.config

/**
 * 配置验证器
 * 用于验证配置参数的合法性
 */
object ConfigValidator {

    /**
     * 验证VideoLayerConfig配置
     */
    fun validateConfig(config: VideoLayerConfig): Result<Unit> {
        return try {
            // 验证预加载数量
            if (config.preloadCount < 0) {
                return Result.failure(IllegalArgumentException("Preload count must be non-negative"))
            }

            // 验证缓存大小
            if (config.cacheSize <= 0) {
                return Result.failure(IllegalArgumentException("Cache size must be positive"))
            }

            // 验证超时时间
            if (config.connectionTimeoutMs <= 0) {
                return Result.failure(IllegalArgumentException("Connection timeout must be positive"))
            }

            if (config.readTimeoutMs <= 0) {
                return Result.failure(IllegalArgumentException("Read timeout must be positive"))
            }

            // 验证重试次数
            if (config.maxRetryCount < 0) {
                return Result.failure(IllegalArgumentException("Max retry count must be non-negative"))
            }

            // 验证缓冲配置
            validateBufferConfig(config.bufferConfig).getOrThrow()

            // 验证手势配置
            validateGestureConfig(config.gestureConfig).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 验证缓冲配置
     */
    private fun validateBufferConfig(config: BufferConfig): Result<Unit> {
        if (config.minBufferMs < 0) {
            return Result.failure(IllegalArgumentException("Min buffer time must be non-negative"))
        }

        if (config.maxBufferMs <= config.minBufferMs) {
            return Result.failure(IllegalArgumentException("Max buffer time must be greater than min buffer time"))
        }

        if (config.bufferForPlaybackMs < 0) {
            return Result.failure(IllegalArgumentException("Buffer for playback must be non-negative"))
        }

        if (config.bufferForPlaybackAfterRebufferMs < 0) {
            return Result.failure(IllegalArgumentException("Buffer for playback after rebuffer must be non-negative"))
        }

        return Result.success(Unit)
    }

    /**
     * 验证手势配置
     */
    private fun validateGestureConfig(config: GestureConfig): Result<Unit> {
        if (config.longPressFastForwardSpeed <= 0) {
            return Result.failure(IllegalArgumentException("Fast forward speed must be positive"))
        }

        if (config.gestureSensitivity !in 0.1f..2.0f) {
            return Result.failure(IllegalArgumentException("Gesture sensitivity must be between 0.1 and 2.0"))
        }

        if (config.minSwipeDistance <= 0) {
            return Result.failure(IllegalArgumentException("Min swipe distance must be positive"))
        }

        if (config.controlsAutoHideDelayMs < 0) {
            return Result.failure(IllegalArgumentException("Controls auto hide delay must be non-negative"))
        }

        return Result.success(Unit)
    }
}