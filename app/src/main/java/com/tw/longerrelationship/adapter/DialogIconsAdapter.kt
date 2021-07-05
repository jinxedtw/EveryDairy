package com.tw.longerrelationship.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R

/**
 * 传入Icon的图标list和点击回调
 */
class DialogIconsAdapter
    (
    private val iconsList: List<Int>,
    private val iconClick: (Drawable, Int) -> Unit
) :
    RecyclerView.Adapter<DialogIconsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dairy_icon, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivIcon.setBackgroundResource(iconsList[position])

        holder.icon.setOnClickListener {
            iconClick(holder.ivIcon.background, iconsList[position])
        }
    }

    override fun getItemCount(): Int {
        return iconsList.size
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        val icon: FrameLayout = itemView.findViewById(R.id.fl_icon)
    }
}