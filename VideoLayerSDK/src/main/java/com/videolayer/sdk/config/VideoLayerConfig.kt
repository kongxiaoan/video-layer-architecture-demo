package com.videolayer.sdk.config

/**
 * VideoLayerSDK 配置类
 * 用于配置SDK的各种参数和行为
 */
data class VideoLayerConfig(
    /**
     * 是否启用自动播放
     */
    val enableAutoPlay: Boolean = true,

    /**
     * 预加载视频数量
     */
    val preloadCount: Int = 3,

    /**
     * 缓存大小（字节）
     */
    val cacheSize: Long = 50 * 1024 * 1024, // 50MB

    /**
     * 是否启用硬件加速
     */
    val enableHardwareAcceleration: Boolean = true,

    /**
     * 播放器重试次数
     */
    val maxRetryCount: Int = 3,

    /**
     * 连接超时时间（毫秒）
     */
    val connectionTimeoutMs: Long = 10000,

    /**
     * 读取超时时间（毫秒）
     */
    val readTimeoutMs: Long = 30000,

    /**
     * 是否启用调试模式
     */
    val enableDebugMode: Boolean = false,

    /**
     * 视频播放质量
     */
    val videoQuality: VideoQuality = VideoQuality.AUTO,

    /**
     * 音频焦点管理策略
     */
    val audioFocusStrategy: AudioFocusStrategy = AudioFocusStrategy.GAIN_TRANSIENT,

    /**
     * 是否启用后台播放
     */
    val enableBackgroundPlay: Boolean = false,

    /**
     * 缓冲策略配置
     */
    val bufferConfig: BufferConfig = BufferConfig(),

    /**
     * 手势配置
     */
    val gestureConfig: GestureConfig = GestureConfig(),

    /**
     * 网络策略
     */
    val networkStrategy: NetworkStrategy = NetworkStrategy.WIFI_AND_MOBILE
) {

    /**
     * 视频播放质量枚举
     */
    enum class VideoQuality {
        AUTO,           // 自动选择
        LOW,            // 低质量 360p
        MEDIUM,         // 中等质量 720p
        HIGH,           // 高质量 1080p
        ULTRA_HIGH      // 超高清 4K
    }

    /**
     * 音频焦点策略
     */
    enum class AudioFocusStrategy {
        GAIN,                    // 长期获取焦点
        GAIN_TRANSIENT,          // 短暂获取焦点
        GAIN_TRANSIENT_MAY_DUCK, // 短暂获取焦点，允许其他应用降低音量
        NONE                     // 不管理音频焦点
    }

    /**
     * 网络策略
     */
    enum class NetworkStrategy {
        WIFI_ONLY,          // 仅WiFi
        MOBILE_ONLY,        // 仅移动网络
        WIFI_AND_MOBILE,    // WiFi和移动网络
        NO_NETWORK          // 离线模式
    }

    companion object {
        /**
         * 默认配置
         */
        val DEFAULT = VideoLayerConfig()

        /**
         * 高性能配置
         */
        val HIGH_PERFORMANCE = VideoLayerConfig(
            enableAutoPlay = true,
            preloadCount = 5,
            cacheSize = 100 * 1024 * 1024,
            enableHardwareAcceleration = true,
            videoQuality = VideoQuality.HIGH,
            bufferConfig = BufferConfig.HIGH_PERFORMANCE
        )

        /**
         * 省电模式配置
         */
        val POWER_SAVING = VideoLayerConfig(
            enableAutoPlay = false,
            preloadCount = 1,
            cacheSize = 20 * 1024 * 1024,
            enableHardwareAcceleration = false,
            videoQuality = VideoQuality.LOW,
            bufferConfig = BufferConfig.POWER_SAVING
        )

        /**
         * WiFi优化配置
         */
        val WIFI_OPTIMIZED = VideoLayerConfig(
            enableAutoPlay = true,
            preloadCount = 5,
            cacheSize = 200 * 1024 * 1024,
            videoQuality = VideoQuality.ULTRA_HIGH,
            networkStrategy = NetworkStrategy.WIFI_ONLY
        )

        /**
         * 创建构建器
         */
        fun builder(): Builder = Builder()
    }

    /**
     * 配置构建器
     */
    class Builder {
        private var enableAutoPlay: Boolean = true
        private var preloadCount: Int = 3
        private var cacheSize: Long = 50 * 1024 * 1024
        private var enableHardwareAcceleration: Boolean = true
        private var maxRetryCount: Int = 3
        private var connectionTimeoutMs: Long = 10000
        private var readTimeoutMs: Long = 30000
        private var enableDebugMode: Boolean = false
        private var videoQuality: VideoQuality = VideoQuality.AUTO
        private var audioFocusStrategy: AudioFocusStrategy = AudioFocusStrategy.GAIN_TRANSIENT
        private var enableBackgroundPlay: Boolean = false
        private var bufferConfig: BufferConfig = BufferConfig()
        private var gestureConfig: GestureConfig = GestureConfig()
        private var networkStrategy: NetworkStrategy = NetworkStrategy.WIFI_AND_MOBILE

        fun enableAutoPlay(enable: Boolean) = apply { this.enableAutoPlay = enable }

        fun setPreloadCount(count: Int) = apply {
            require(count >= 0) { "Preload count must be non-negative" }
            this.preloadCount = count
        }

        fun setCacheSize(size: Long) = apply {
            require(size > 0) { "Cache size must be positive" }
            this.cacheSize = size
        }

        fun enableHardwareAcceleration(enable: Boolean) = apply {
            this.enableHardwareAcceleration = enable
        }

        fun setMaxRetryCount(count: Int) = apply {
            require(count >= 0) { "Max retry count must be non-negative" }
            this.maxRetryCount = count
        }

        fun setConnectionTimeout(timeoutMs: Long) = apply {
            require(timeoutMs > 0) { "Connection timeout must be positive" }
            this.connectionTimeoutMs = timeoutMs
        }

        fun setReadTimeout(timeoutMs: Long) = apply {
            require(timeoutMs > 0) { "Read timeout must be positive" }
            this.readTimeoutMs = timeoutMs
        }

        fun enableDebugMode(enable: Boolean) = apply { this.enableDebugMode = enable }

        fun setVideoQuality(quality: VideoQuality) = apply { this.videoQuality = quality }

        fun setAudioFocusStrategy(strategy: AudioFocusStrategy) = apply {
            this.audioFocusStrategy = strategy
        }

        fun enableBackgroundPlay(enable: Boolean) = apply {
            this.enableBackgroundPlay = enable
        }

        fun setBufferConfig(config: BufferConfig) = apply { this.bufferConfig = config }

        fun setGestureConfig(config: GestureConfig) = apply { this.gestureConfig = config }

        fun setNetworkStrategy(strategy: NetworkStrategy) = apply {
            this.networkStrategy = strategy
        }

        fun build(): VideoLayerConfig {
            return VideoLayerConfig(
                enableAutoPlay = enableAutoPlay,
                preloadCount = preloadCount,
                cacheSize = cacheSize,
                enableHardwareAcceleration = enableHardwareAcceleration,
                maxRetryCount = maxRetryCount,
                connectionTimeoutMs = connectionTimeoutMs,
                readTimeoutMs = readTimeoutMs,
                enableDebugMode = enableDebugMode,
                videoQuality = videoQuality,
                audioFocusStrategy = audioFocusStrategy,
                enableBackgroundPlay = enableBackgroundPlay,
                bufferConfig = bufferConfig,
                gestureConfig = gestureConfig,
                networkStrategy = networkStrategy
            )
        }
    }
}