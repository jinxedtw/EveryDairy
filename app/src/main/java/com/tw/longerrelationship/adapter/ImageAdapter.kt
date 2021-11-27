package com.tw.longerrelationship.adapter

import com.tw.longerrelationship.views.activity.PictureInfoActivity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tw.longerrelationship.R
import com.tw.longerrelationship.views.widgets.PhotoView

/**
 * [PictureInfoActivity]的viewpager的图片展示适配器
 */
class ImageAdapter(private val uriList: List<Uri>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var loadImage: (PhotoView, Uri) -> Unit = { _, _ -> }
    var onImageClick: () -> Unit = {}
    var onImageExit: () -> Unit = {}
    var onAlphaChange: (Float) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.photoView.transitionName = "img_${position}"

        Glide.with(context).asBitmap().load(uriList[position])
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.photoView.initBitMap(bitmap = resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

    override fun getItemCount(): Int = uriList.size


    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: PhotoView = itemView.findViewById(R.id.iv_picture)

        init {
            photoView.setOnClickListener {
                onImageClick()
            }
            photoView.onImageExit = {
                onImageExit()
            }
            photoView.onAlphaChange = {
                onAlphaChange(it)
            }
        }
    }
}