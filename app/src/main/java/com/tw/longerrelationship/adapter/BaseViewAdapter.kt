package com.tw.longerrelationship.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewAdapter<T>(@LayoutRes val layoutId: Int, val data: List<T>) : RecyclerView.Adapter<BaseViewAdapter<T>.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindViewHolder(holder, position, data[position])
    }

    override fun getItemCount(): Int = data.size

    abstract fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: T)
}