package com.tw.longerrelationship.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * 通用Adapter,支持添加头部和尾部布局
 */
abstract class BaseViewAdapter<T> : RecyclerView.Adapter<BaseViewAdapter<T>.ViewHolder> {
    private var rawData: List<T> = emptyList()

    @LayoutRes
    private var bodyLayoutId: Int = EMPTY_LAYOUT_ID

    @LayoutRes
    private var headLayoutId: Int = EMPTY_LAYOUT_ID

    @LayoutRes
    private var tailLayoutId: Int = EMPTY_LAYOUT_ID

    constructor(data: List<T>, @LayoutRes bodyId: Int, @LayoutRes headId: Int = EMPTY_LAYOUT_ID, @LayoutRes tailId: Int = EMPTY_LAYOUT_ID) {
        rawData = data
        bodyLayoutId = bodyId
        headLayoutId = headId
        tailLayoutId = tailId
    }

    /**
     * 使用该构造方法需要实现[MultiItemTypeSupport]接口
     */
    constructor(data: List<T>, @LayoutRes headId: Int = EMPTY_LAYOUT_ID, @LayoutRes tailId: Int = EMPTY_LAYOUT_ID) {
        rawData = data
        headLayoutId = headId
        tailLayoutId = tailId
    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        var binding: ViewBinding? = null

        constructor(itemView: View) : super(itemView)

        constructor(viewBinding: ViewBinding) : super(viewBinding.root) {
            binding = viewBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        return when (viewType) {
            HEAD_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(headLayoutId, parent, false))
            }
            TAIL_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(tailLayoutId, parent, false))
            }
            BODY_LAYOUT -> {
                view = LayoutInflater.from(parent.context).inflate(bodyLayoutId, parent, false)
                if (useViewBinding(view) != null) {
                    ViewHolder(useViewBinding(view)!!)
                } else {
                    ViewHolder(view)
                }
            }
            else -> {
                if (this is MultiItemTypeSupport<*>) {
                    view = LayoutInflater.from(parent.context).inflate(this.getLayoutId(viewType), parent, false)
                    if (this.useViewBinding(view, viewType) != null) {
                        ViewHolder(this.useViewBinding(view, viewType)!!)
                    } else {
                        ViewHolder(view)
                    }
                } else {
                    view = LayoutInflater.from(parent.context).inflate(bodyLayoutId, parent, false)
                    if (useViewBinding(view) != null) {
                        ViewHolder(useViewBinding(view)!!)
                    } else {
                        ViewHolder(view)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEAD_LAYOUT -> {
                bindHeadViewHolder(holder, position)
            }
            TAIL_LAYOUT -> {
                bindTailViewHolder(holder, position)
            }
            BODY_LAYOUT -> {
                bindViewHolder(holder, position, rawData[getRealPosition(position)])
            }
            else -> {
                if (this is MultiItemTypeSupport<*>) {
                    (this as MultiItemTypeSupport<T>).bindMultiViewHolder(holder, position, rawData[getRealPosition(position)], getItemViewType(position))
                } else {
                    bindViewHolder(holder, position, rawData[getRealPosition(position)])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        var count = rawData.size
        if (headLayoutId != EMPTY_LAYOUT_ID) {
            count++
        }
        if (tailLayoutId != EMPTY_LAYOUT_ID) {
            count++
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && headLayoutId != EMPTY_LAYOUT_ID) {
            return HEAD_LAYOUT
        }
        if (position == itemCount - 1 && tailLayoutId != EMPTY_LAYOUT_ID) {
            return TAIL_LAYOUT
        }
        if (this is MultiItemTypeSupport<*>) {
            return (this as MultiItemTypeSupport<T>).getItemViewType(position, rawData[getRealPosition(position)])
        }
        return BODY_LAYOUT
    }

    private fun getRealPosition(position: Int): Int {
        return if (headLayoutId != EMPTY_LAYOUT_ID) {
            position - 1
        } else {
            position
        }
    }

    /**
     * 单一数据源获取到不同type类型
     */
    interface MultiItemTypeSupport<T> {
        fun getLayoutId(type: Int): Int
        fun getItemViewType(position: Int, item: T): Int
        fun bindMultiViewHolder(holder: BaseViewAdapter<T>.ViewHolder, position: Int, item: T, type: Int)
        fun useViewBinding(itemView: View, type: Int): ViewBinding? = null
    }

    // 重写下面方法进行扩展-------------------------------------------------------------------------------- start

    /** 重写这两个方法实现定制化头部和尾部 */
    open fun bindHeadViewHolder(holder: ViewHolder, position: Int) {}

    open fun bindTailViewHolder(holder: ViewHolder, position: Int) {}

    /** 使用binding的方式进行布局绑定,只支持单一的type */
    open fun useViewBinding(itemView: View): ViewBinding? = null

    /** 只支持绑定BODY_LAYOUT的布局内容 */
    open fun bindViewHolder(holder: ViewHolder, position: Int, item: T) {}

    // ------------------------------------------------------------------------------------------------- end

    companion object {
        const val EMPTY_LAYOUT_ID = -1

        const val HEAD_LAYOUT = 10000
        const val BODY_LAYOUT = 10001
        const val TAIL_LAYOUT = 10002
    }
}