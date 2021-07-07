package com.tw.longerrelationship.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tw.longerrelationship.R
import com.tw.longerrelationship.views.activity.DairyInfoActivity


/**
 *  日记详情界面展示图片的适配器
 *  @see[DairyInfoActivity]
 */
class PictureShowAdapter(
    private val pictureList: List<Uri>,
    private val activity: DairyInfoActivity
) : RecyclerView.Adapter<PictureShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_picture_select, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity)
            .load(pictureList[position])
            .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
            .into(holder.picture)

        holder.picture.tag = position
    }

    override fun getItemCount(): Int = pictureList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.iv_picture)

        init {
            picture.setOnClickListener {

                activity.pictureInfoActivityJump(it.tag as Int)
            }
        }
    }
}