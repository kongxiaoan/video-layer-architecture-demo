package com.videolayer.sdk.layer

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.videolayer.sdk.core.VideoLayer
import com.videolayer.sdk.databinding.VideolayerGestureLayerBinding
import com.videolayer.sdk.event.LayerEvent

class VideoGestureLayer : VideoLayer() {
    private var binding: VideolayerGestureLayerBinding? = null
    private lateinit var gestureDetector: GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(context: Context): View {
        VideolayerGestureLayerBinding.inflate(LayoutInflater.from(context)).also { vb ->
            binding = vb

            // 初始化手势检测器
            gestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                        sendEvent(LayerEvent.SingleTap(getVideoId()))
                        // 可做暂停/恢复
                        return true
                    }

                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        sendEvent(LayerEvent.DoubleTap(getVideoId()))
                        // 可做点赞
                        return true
                    }

                    override fun onLongPress(e: MotionEvent) {
                        sendEvent(LayerEvent.LongPress(getVideoId()))
                        // 可做弹出菜单
                    }

                    override fun onScroll(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        distanceX: Float,
                        distanceY: Float
                    ): Boolean {
                        sendEvent(
                            LayerEvent.Scroll(
                                getVideoId(),
                                mapOf(
                                    "id" to currentVideoItem?.id,
                                    "dx" to distanceX,
                                    "dy" to distanceY
                                )
                            )
                        )
                        // 可做快进/音量/亮度调节等
                        return true
                    }

                    override fun onFling(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float
                    ): Boolean {
                        sendEvent(
                            LayerEvent.Fling(
                                getVideoId(),
                                mapOf(
                                    "id" to currentVideoItem?.id,
                                    "vx" to velocityX,
                                    "vy" to velocityY
                                )
                            )
                        )
                        // 可做下滑/上滑退出等
                        return true
                    }

                    override fun onDown(e: MotionEvent): Boolean {
                        sendEvent(LayerEvent.Down(getVideoId()))
                        return true
                    }

                    override fun onShowPress(e: MotionEvent) {
                        sendEvent(LayerEvent.ShowPress(getVideoId()))
                    }
                })

            // 覆盖整个短视频区域的透明手势层
            vb.root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }

            return vb.root
        }
    }

    private fun getVideoId(): String {
        return currentVideoItem?.id ?: throw IllegalStateException("当前视频项未设置")
    }

    /**
     * 可在此层消费部分事件
     */
    override fun onReceiveEvent(event: LayerEvent): Boolean {
        return super.onReceiveEvent(event)
    }
}