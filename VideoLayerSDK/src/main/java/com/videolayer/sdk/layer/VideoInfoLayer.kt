package com.videolayer.sdk.layer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.videolayer.sdk.core.VideoLayer
import com.videolayer.sdk.data.VideoItem
import com.videolayer.sdk.databinding.VideolayerInfoLayerBinding

class VideoInfoLayer : VideoLayer() {
    private var binding: VideolayerInfoLayerBinding? = null
    override fun onCreateView(context: Context): View {
        VideolayerInfoLayerBinding.inflate(LayoutInflater.from(context)).also {
            binding = it
            return it.root
        }
    }

    override fun onBindData(videoItem: VideoItem) {
        super.onBindData(videoItem)
        binding?.ivCover?.tag = videoItem.videoUrl
        binding?.tvTitle?.text = videoItem.title
    }
}