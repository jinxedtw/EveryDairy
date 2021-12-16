package com.tw.longerrelationship.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.dp2px
import com.tw.longerrelationship.util.getScreenWidth
import com.tw.longerrelationship.views.activity.DairyInfoActivity


/**
 *  日记详情界面展示图片的适配器
 *  @see[DairyInfoActivity]
 */
class PictureShowAdapter(
    private val pictureList: List<String>,
    private val context: Context
) : RecyclerView.Adapter<PictureShowAdapter.ViewHolder>() {
    var onItemClick: (View) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_picture_select, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.picture.transitionName = "img_${position}"
        holder.picture.tag = position
        holder.picture.setOnClickListener {
            onItemClick.invoke(it)
        }

        if (pictureList.size == 1) {
            // 当只有一张图片时大图显示
            val param = holder.itemView.layoutParams
            param.width = getScreenWidth() / 3 * 2
            param.height = param.width
            holder.itemView.layoutParams = param
        }
        Glide.with(context)
            .load(pictureList[position])
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(dp2px(5))))
            .into(holder.picture)

    }

    override fun getItemCount(): Int = pictureList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.iv_picture)
    }
}