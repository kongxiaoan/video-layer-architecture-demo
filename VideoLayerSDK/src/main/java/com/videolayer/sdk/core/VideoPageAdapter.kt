package com.videolayer.sdk.core

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.layer.VideoButtonLayer
import com.videolayer.sdk.layer.VideoGestureLayer
import com.videolayer.sdk.layer.VideoInfoLayer

/**
 * 视频页面适配器，每页一个视频
 */
class VideoPageAdapter : ListAdapter<VideoItem, VideoPageAdapter.VideoViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val host = VideoLayerHost(parent.context)
        host.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        host.addLayer(VideoGestureLayer())
        host.addLayer(VideoInfoLayer()) // 默认添加一个信息图层
        host.addLayer(VideoButtonLayer())
        return VideoViewHolder(host)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)
        holder.host.bindData(item)
        // 这里只负责数据绑定，图层添加交由业务方实现
    }

    class VideoViewHolder(val host: VideoLayerHost) : RecyclerView.ViewHolder(host)
}