package com.tw.longerrelationship.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.ImageFolder

class ThumbnailAdapter(val context: Context, layoutId: Int, data: List<ImageFolder>) : BaseViewAdapter<ImageFolder>(data, bodyId = layoutId) {

    var onItemClick: (ImageFolder, Int) -> Unit = { _, _ -> }

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: ImageFolder) {
        val thumbnailImage = holder.itemView.findViewById<ImageView>(R.id.iv_thumbnail)
        val thumbnailName = holder.itemView.findViewById<TextView>(R.id.tv_thumbnail_name)
        val thumbnailCount = holder.itemView.findViewById<TextView>(R.id.tv_thumbnail_count)

        thumbnailName.text = item.folderName
        thumbnailCount.text = String.format("${item.mAlbumFiles.size}张")
        Glide.with(context).load(item.thumbnailImage).centerCrop().into(thumbnailImage)

        holder.itemView.setOnClickListener {
            onItemClick(item, position)
        }
    }


}