package com.tw.longerrelationship.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.ImageFolder
import com.tw.longerrelationship.util.getScreenWidth

class AlbumAdapter(val context: Context, var data: List<String>) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    var onCheckBoxClick: (String,Boolean) -> Unit = { _, _ -> }
    var onItemClick: (String, CheckBox) -> Unit = { _, _ -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_album_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photoParam = holder.mPhoto.layoutParams
        photoParam.width = getScreenWidth() / GLIDE_LAYOUT_COUNT - 10
        photoParam.height = photoParam.width
        holder.mPhoto.layoutParams = photoParam

        Glide.with(context).load(data[position])
            .centerCrop()
            .into(holder.mPhoto)

        // 点击复选框
        holder.mCheckBox.setOnClickListener {
            onCheckBoxClick(data[position],holder.mCheckBox.isChecked)
        }

        // 点击图片
        holder.itemView.setOnClickListener {
            onItemClick(data[position], holder.mCheckBox)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPhoto: ImageView = itemView.findViewById(R.id.iv_photo)
        val mCheckBox: CheckBox = itemView.findViewById(R.id.album_checkbox)
    }

    companion object {
        const val GLIDE_LAYOUT_COUNT = 4
    }
}