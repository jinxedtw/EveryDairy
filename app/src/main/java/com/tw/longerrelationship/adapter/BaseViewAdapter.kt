package com.tw.longerrelationship.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 *  通用Adapter,支持添加头部和尾部布局
 */
abstract class BaseViewAdapter<T>(
    @LayoutRes val layoutId: Int,
    val data: List<T>,
    private val headLayout: Int = EMPTY_LAYOUT,            // 头部布局ID
    private val tailLayout: Int = EMPTY_LAYOUT             // 尾部布局ID
) : RecyclerView.Adapter<BaseViewAdapter<T>.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            HEAD_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(headLayout, parent, false))
            }
            TAIL_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(tailLayout, parent, false))
            }
            BODY_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEAD_LAYOUT -> {
                bindHeadViewHolder()
            }
            TAIL_LAYOUT -> {
                bindTailViewHolder()
            }
            BODY_LAYOUT -> {
                bindViewHolder(holder, position, data[position])
            }
            else -> {
                bindViewHolder(holder, position, data[position])
            }
        }
    }

    /** 重写这两个方法实现定制化头部和尾部 */
    open fun bindHeadViewHolder() {}

    open fun bindTailViewHolder() {}

    override fun getItemCount(): Int {
        var count = data.size
        if (headLayout != EMPTY_LAYOUT) {
            count++
        }
        if (tailLayout != EMPTY_LAYOUT) {
            count++
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && headLayout != EMPTY_LAYOUT) {
            return HEAD_LAYOUT
        }
        if (position == itemCount - 1 && tailLayout != EMPTY_LAYOUT) {
            return TAIL_LAYOUT
        }
        return BODY_LAYOUT
    }

    /** 只支持绑定Body_LAYOUT的布局内容 */
    abstract fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: T)

    companion object {
        const val EMPTY_LAYOUT = -1
        const val HEAD_LAYOUT = 100
        const val BODY_LAYOUT = 101
        const val TAIL_LAYOUT = 102
    }
}