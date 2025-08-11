package com.videolayer.sdk.layer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.videolayer.sdk.core.VideoLayer
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.databinding.VideolayerButtonLayerBinding
import com.videolayer.sdk.event.LayerEvent

class VideoButtonLayer : VideoLayer() {
    private var binding: VideolayerButtonLayerBinding? = null

    override val priority: Int = 90

    override fun onCreateView(context: Context): View {
        VideolayerButtonLayerBinding.inflate(LayoutInflater.from(context)).also {
            binding = it

            it.btnLike.setOnClickListener {
                Toast.makeText(context, "点赞: ${currentVideoItem?.title}", Toast.LENGTH_SHORT).show()
                sendEvent(LayerEvent("like", currentVideoItem?.id))
            }

            it.btnShare.setOnClickListener {
                Toast.makeText(context, "分享: ${currentVideoItem?.title}", Toast.LENGTH_SHORT).show()
                sendEvent(LayerEvent("share", currentVideoItem?.id))
            }

            return it.root
        }
    }

    override fun onBindData(videoItem: VideoItem) {
        super.onBindData(videoItem)
        // 可以根据视频数据设置按钮状态
    }
}