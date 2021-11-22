package com.tw.longerrelationship.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.R
import com.tw.longerrelationship.help.DairyColorHelper.colorList
import com.tw.longerrelationship.util.gone
import com.tw.longerrelationship.util.visible
import com.tw.longerrelationship.views.widgets.ColorsPainDialog

class ColorItemSelectAdapter(
    private val colorsPainDialog: ColorsPainDialog,
    private val context: Context,
    var currentIndex: Int
) :
    RecyclerView.Adapter<ColorItemSelectAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_color_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, colorList[position]))

        holder.itemView.setOnClickListener {
            colorsPainDialog.onColorSelect(colorList[position], position)
        }

        if (position == currentIndex) {
            holder.colorSelectImage.visible()
        } else {
            holder.colorSelectImage.gone()
        }
    }

    override fun getItemCount() = colorList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorSelectImage: ImageView = itemView.findViewById(R.id.iv_color_select)
    }
}