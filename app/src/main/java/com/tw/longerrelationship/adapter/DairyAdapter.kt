package com.tw.longerrelationship.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.util.getComparedTime
import com.tw.longerrelationship.util.getNowTimeHour
import com.tw.longerrelationship.views.activity.DairyInfoActivity
import com.tw.longerrelationship.views.activity.MainActivity
import org.w3c.dom.Text
import java.util.*

class DairyAdapter(val context: Context, var type: Int = 1) :
    PagingDataAdapter<DairyItem, RecyclerView.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == FOLD_LAYOUT) FoldViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dairy_fold_content, parent, false)
        )
        else UnfoldViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dairy_unfold_content, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dairyItem = getItem(position) ?: return

        when (holder) {
            is FoldViewHolder -> {
                val content = StringBuilder()

                holder.time.text = getComparedTime(dairyItem.time)

                for (i in dairyItem.uriList.indices)
                    content.append("[图片]")
                if (dairyItem.content != null)
                    content.append(dairyItem.content)
                holder.content.text = content

                if (dairyItem.title?.isNotEmpty() == true) {
                    holder.title.visibility = View.VISIBLE
                    holder.title.text = dairyItem.title
                }
            }

            is UnfoldViewHolder -> {
                holder.time.text = getComparedTime(dairyItem.time)
                if (dairyItem.uriList.isNotEmpty()) {
                    Glide.with(this.context).load(dairyItem.uriList[0]).into(holder.picture)
                } else {
                    holder.picture.visibility = View.GONE
                }

                if (dairyItem.content != null)
                    holder.content.text = dairyItem.content
            }
        }

        holder.itemView.setOnClickListener {
            (context as Activity).startActivity(
                Intent(
                    context,
                    DairyInfoActivity::class.java
                ).apply {
                    putExtra("dairyId", dairyItem.id)
                }
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    companion object {
        const val FOLD_LAYOUT = 1
        const val UNFOLD_LAYOUT = 2

        private val COMPARATOR = object : DiffUtil.ItemCallback<DairyItem>() {
            override fun areItemsTheSame(oldItem: DairyItem, newItem: DairyItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DairyItem, newItem: DairyItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class FoldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.tv_dairy_content)
        val time: TextView = itemView.findViewById(R.id.tv_time)
        val title: TextView = itemView.findViewById(R.id.tv_title)
    }

    inner class UnfoldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.iv_photo)
        val content: TextView = itemView.findViewById(R.id.tv_content)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }


}