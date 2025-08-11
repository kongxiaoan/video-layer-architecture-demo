package com.videolayer.sdk

import android.content.Context
import com.videolayer.sdk.config.VideoLayerConfig
import com.videolayer.sdk.utils.VideoLogger
import java.io.File

/**
 * VideoLayerSDK 入口
 * 提供SDK初始化和核心API
 */
object VideoLayerSDK {
    private const val TAG = "VideoLayerSDK"
    private var isInitialized = false
    private var globalConfig: VideoLayerConfig? = null

    fun initialize(
        context: Context,
        config: VideoLayerConfig = VideoLayerConfig.DEFAULT,
        logConfig: VideoLogger.LogConfig = VideoLogger.LogConfig()
    ) {
        if (isInitialized) {
            return
        }
        // 初始化日志系统
        VideoLogger.initialize(context, logConfig)
        VideoLogger.i(TAG, "开始初始化VideoLayerSDK")

        this.globalConfig = config
        isInitialized = true
        VideoLogger.i(TAG, "VideoLayerSDK初始化完成")
        VideoLogger.d(TAG, "SDK配置: $config")
    }

    /*
        * 获取加密的日志文件
        */
    suspend fun getLogFile(): File? {
        checkInitialized()
        VideoLogger.i(TAG, "开始生成日志文件")
        return VideoLogger.getEncryptedLogZip()
    }

    /**
     * 清理日志文件
     */
    fun clearLogs() {
        checkInitialized()
        VideoLogger.i(TAG, "清理日志文件")
        VideoLogger.clearLogs()
    }

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("VideoLayerSDK must be initialized before use")
        }
    }
}