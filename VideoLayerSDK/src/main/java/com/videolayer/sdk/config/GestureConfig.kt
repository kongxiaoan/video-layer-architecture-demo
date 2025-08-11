package com.videolayer.sdk.config

/**
 * 手势配置类
 */
data class GestureConfig(
    /**
     * 是否启用双击暂停/播放
     */
    val enableDoubleTapToPause: Boolean = true,

    /**
     * 是否启用左右滑动调节进度
     */
    val enableHorizontalSwipeForProgress: Boolean = true,

    /**
     * 是否启用上下滑动调节音量（右侧）
     */
    val enableVerticalSwipeForVolume: Boolean = true,

    /**
     * 是否启用上下滑动调节亮度（左侧）
     */
    val enableVerticalSwipeForBrightness: Boolean = true,

    /**
     * 是否启用单击显示/隐藏控制栏
     */
    val enableSingleTapToShowControls: Boolean = true,

    /**
     * 是否启用长按倍速播放
     */
    val enableLongPressForFastForward: Boolean = true,

    /**
     * 长按倍速播放的倍率
     */
    val longPressFastForwardSpeed: Float = 2.0f,

    /**
     * 手势敏感度 (0.1 - 2.0)
     */
    val gestureSensitivity: Float = 1.0f,

    /**
     * 最小滑动距离（像素）
     */
    val minSwipeDistance: Int = 50,

    /**
     * 控制栏自动隐藏时间（毫秒）
     */
    val controlsAutoHideDelayMs: Long = 5000,

    /**
     * 是否启用手势反馈（震动）
     */
    val enableGestureFeedback: Boolean = true
) {

    init {
        require(longPressFastForwardSpeed > 0) { "Fast forward speed must be positive" }
        require(gestureSensitivity in 0.1f..2.0f) { "Gesture sensitivity must be between 0.1 and 2.0" }
        require(minSwipeDistance > 0) { "Min swipe distance must be positive" }
        require(controlsAutoHideDelayMs >= 0) { "Controls auto hide delay must be non-negative" }
    }

    companion object {
        /**
         * 默认手势配置
         */
        val DEFAULT = GestureConfig()

        /**
         * 精简模式（只保留基础手势）
         */
        val SIMPLE = GestureConfig(
            enableDoubleTapToPause = true,
            enableHorizontalSwipeForProgress = false,
            enableVerticalSwipeForVolume = false,
            enableVerticalSwipeForBrightness = false,
            enableSingleTapToShowControls = true,
            enableLongPressForFastForward = false
        )

        /**
         * 全功能模式
         */
        val FULL_FEATURED = GestureConfig(
            enableDoubleTapToPause = true,
            enableHorizontalSwipeForProgress = true,
            enableVerticalSwipeForVolume = true,
            enableVerticalSwipeForBrightness = true,
            enableSingleTapToShowControls = true,
            enableLongPressForFastForward = true,
            longPressFastForwardSpeed = 2.5f,
            gestureSensitivity = 1.2f
        )

        /**
         * 禁用所有手势
         */
        val DISABLED = GestureConfig(
            enableDoubleTapToPause = false,
            enableHorizontalSwipeForProgress = false,
            enableVerticalSwipeForVolume = false,
            enableVerticalSwipeForBrightness = false,
            enableSingleTapToShowControls = false,
            enableLongPressForFastForward = false
        )
    }
}