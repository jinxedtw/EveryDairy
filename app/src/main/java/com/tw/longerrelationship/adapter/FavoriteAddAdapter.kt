package com.tw.longerrelationship.adapter

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R

class FavoriteAddAdapter(val context: Context, layoutId: Int, data: List<Int>) : BaseViewAdapter<Int>(layoutId, data) {
    private var selectedImage = -1
    var notebookCoverOnSelect: (Int) -> Unit = { }

    override fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: Int) {
        holder.itemView.findViewById<ImageView>(R.id.iv_notebook_icon).background = ContextCompat.getDrawable(context, item)

        if (item == selectedImage) {
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.bg_favorites_selected)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.bg_favorite_item)
        }

        holder.itemView.setOnClickListener {
            notebookCoverOnSelect.invoke(item)
            selectedImage = item
            notifyDataSetChanged()
        }
    }
}