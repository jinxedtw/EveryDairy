package com.tw.longerrelationship.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.NotebookItem
import com.tw.longerrelationship.viewmodel.FavoritesViewModel

class FavoriteItemAdapter(val context: Context, layoutId: Int, data: List<NotebookItem>, tailLayout: Int) :
    BaseViewAdapter<NotebookItem>(layoutId, data, tailLayoutId = tailLayout) {

    var notebookOnClick: () -> Unit = { }
    var notebookOnAdd: () -> Unit = { }
    private var selectedPosition = 0

    override fun bindViewHolder(holder: ViewHolder, position: Int, item: NotebookItem) {
        val notebookNameTv = holder.itemView.findViewById<TextView>(R.id.tv_notebook_name)
        val notebookIconIv = holder.itemView.findViewById<ImageView>(R.id.iv_notebook_icon)
        val notebookCountTv = holder.itemView.findViewById<TextView>(R.id.tv_notebook_count)

        notebookNameTv.text = item.notebookName
        notebookIconIv.setBackgroundResource(item.notebookImg)
        notebookCountTv.text = item.notebookCount.toString()

        if (position == selectedPosition) {
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.bg_favorites_selected)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.bg_favorite_item)
        }

        holder.itemView.setOnClickListener {
            notebookOnClick()
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun bindTailViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            notebookOnAdd()
        }
    }
}