package com.videolayer.sdk.core

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.videolayer.sdk.data.VideoItem

/**
 * 视频列表视图容器（支持竖直滑动列表播放）
 */
class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewPager: ViewPager2
    private val adapter: VideoPageAdapter

    init {
        viewPager = ViewPager2(context).apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        adapter = VideoPageAdapter()
        viewPager.adapter = adapter
        addView(viewPager)
    }

    /**
     * 绑定视频数据列表
     */
    fun bindData(videoItems: List<VideoItem>) {
        adapter.submitList(videoItems)
    }

    /**
     * 设置当前页面
     */
    fun setCurrentItem(position: Int, smoothScroll: Boolean = true) {
        viewPager.setCurrentItem(position, smoothScroll)
    }

    /**
     * 获取当前页面位置
     */
    fun getCurrentItem(): Int = viewPager.currentItem

    /**
     * 注册页面变化监听器
     */
    fun registerOnPageChangeCallback(callback: ViewPager2.OnPageChangeCallback) {
        viewPager.registerOnPageChangeCallback(callback)
    }

    fun unregisterOnPageChangeCallback(callback: ViewPager2.OnPageChangeCallback) {
        viewPager.unregisterOnPageChangeCallback(callback)
    }
}