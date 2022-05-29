package com.tw.longerrelationship.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.help.GridItemDecoration
import com.tw.longerrelationship.util.dp2px


class PictureItemAdapter(val context: Context, layoutId: Int, data: List<String>, val pictureMap: MutableMap<String, List<String>>) :
    BaseViewAdapter<String>(data, bodyId = layoutId, tailId = R.layout.item_dairy_tail) {

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(holder: ViewHolder, position: Int, item: String) {
        val segment = item.split(".")           // eg: 2022.1.13.周四

        val pictureDay = holder.itemView.findViewById<TextView>(R.id.tv_day_of_month)
        val pictureYearAndMonth = holder.itemView.findViewById<TextView>(R.id.tv_year_and_month)
        val pictureWeek = holder.itemView.findViewById<TextView>(R.id.tv_week)
        val pictureInfoItem = holder.itemView.findViewById<RecyclerView>(R.id.rv_pictures)

        pictureDay.text = segment[2]
        pictureYearAndMonth.text = "${segment[0]}年${segment[1]}月"
        pictureWeek.text = segment[3]
        pictureInfoItem.layoutManager = GridLayoutManager(context, 3)
        pictureInfoItem.addItemDecoration(GridItemDecoration(3, dp2px(10)))
        pictureInfoItem.adapter = PictureItemInfoAdapter(context, item, R.layout.item_picture_info, pictureMap[item]!!)
    }
}