package com.tw.longerrelationship.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R

class ImageAdapter(private val uriList: List<Uri>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context).load(uriList[position]).into(holder.imageView)
    }

    override fun getItemCount(): Int = uriList.size


    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_show_picture)

        init {
            imageView.setOnClickListener {
                (context as Activity).finish()
            }
        }
    }
}