package com.tw.longerrelationship.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R

class DrawerItemAdapter(var list: List<DrawerLayoutBean>) : RecyclerView.Adapter<DrawerItemAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    var listener: OnItemClickListener? = null
    fun setClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.mImgDrawerItem.setImageResource(list[position].icon)
        holder.mImgDrawerItem.setColorFilter(Color.BLACK)
        holder.mTvDrawerText.setText(list[position].title)
        holder.itemView.setOnClickListener {
            if (listener != null) {
                listener?.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImgDrawerItem: ImageView = itemView.findViewById(R.id.icon_drawer_item_imageView)
        val mTvDrawerText: TextView = itemView.findViewById(R.id.text_drawer_item_textView)
    }

    data class DrawerLayoutBean(
        @StringRes
        var title: Int,
        @DrawableRes
        var icon: Int,
        var type: Int,
    )

    companion object {
        const val DRAWER_PICTURE = 1
        const val DRAWER_DAILY = 2
        const val DRAWER_FAVORITES = 3
        const val DRAWER_SECRET = 4
        const val DRAWER_HELP = 5
        const val DRAWER_ABOUT = 6
        const val DRAWER_SETTING = 7
        const val DRAWER_COUNTDOWN_DAY=8
    }
}