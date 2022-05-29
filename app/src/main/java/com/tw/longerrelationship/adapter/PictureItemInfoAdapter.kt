package com.tw.longerrelationship.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.Constants
import com.tw.longerrelationship.util.dp2px
import com.tw.longerrelationship.views.activity.AlbumActivity
import com.tw.longerrelationship.views.activity.PictureInfoActivity
import java.util.ArrayList

class PictureItemInfoAdapter(val context: Context, val pictureTime: String, layoutId: Int,val data: List<String>) : BaseViewAdapter<String>(data, bodyId = layoutId) {

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: String) {
        val picture = holder.itemView.findViewById<ImageView>(R.id.iv_picture_info)

        Glide.with(context).load(item).apply(RequestOptions().transform(CenterCrop(), RoundedCorners(dp2px(5)))).into(picture)

        picture.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList(Constants.INTENT_PICTURE_LIST, data.toMutableList() as ArrayList<out Parcelable>)
                putInt(Constants.INTENT_CURRENT_PICTURE, position)
                putString(Constants.INTENT_PICTURE_TIME, pictureTime)
                putBoolean(Constants.INTENT_IF_CAN_DELETE, false)
            }

            (context as AlbumActivity).startActivity(Intent(context, PictureInfoActivity::class.java).apply {
                putExtras(bundle)
            })
        }
    }
}