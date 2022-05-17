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
abstract class BaseViewAdapter<T>(
    @LayoutRes private val layoutId: Int,
    private val rawData: List<T> = emptyList(),
    @LayoutRes private val headLayoutId: Int = EMPTY_LAYOUT_ID,            // 头部布局ID
    @LayoutRes private val tailLayoutId: Int = EMPTY_LAYOUT_ID             // 尾部布局ID
) : RecyclerView.Adapter<BaseViewAdapter<T>.ViewHolder>() {

    private val typeToLayoutMap = hashMapOf<Int, @LayoutRes Int>()

    inner class ViewHolder : RecyclerView.ViewHolder {
        var binding: ViewBinding? = null

        constructor(itemView: View) : super(itemView)
        constructor(viewBinding: ViewBinding) : super(viewBinding.root) {
            binding = viewBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            HEAD_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(headLayoutId, parent, false))
            }
            TAIL_LAYOUT -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(tailLayoutId, parent, false))
            }
            BODY_LAYOUT -> {
                val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
                if (useViewBinding(view) != null) {
                    ViewHolder(useViewBinding(view)!!)
                } else {
                    ViewHolder(view)
                }
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
                if (useViewBinding(view) != null) {
                    ViewHolder(useViewBinding(view)!!)
                } else {
                    ViewHolder(view)
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
                bindViewHolder(holder, position, rawData[position])
            }
            else -> {
                bindViewHolder(holder, position, rawData[position])
            }
        }
    }

    /** 只支持绑定BODY_LAYOUT的布局内容 */
    abstract fun bindViewHolder(holder: ViewHolder, position: Int, item: T)

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
        return getLayoutType()
    }

    private fun getLayoutType(): Int {
        return BODY_LAYOUT
    }

    // 重写下面方法进行扩展-------------------------------------------------------------------------------- start

    /** 重写这两个方法实现定制化头部和尾部 */
    open fun bindHeadViewHolder(holder: ViewHolder, position: Int) {}

    open fun bindTailViewHolder(holder: ViewHolder, position: Int) {}

    /** 使用binding的方式进行布局绑定 */
    open fun useViewBinding(itemView: View): ViewBinding? = null

    // ------------------------------------------------------------------------------------------------- end

    companion object {
        const val EMPTY_LAYOUT_ID = -1

        const val HEAD_LAYOUT = 100
        const val BODY_LAYOUT = 101
        const val TAIL_LAYOUT = 102
    }
}