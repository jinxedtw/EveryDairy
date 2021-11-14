package com.tw.longerrelationship.adapter

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.help.TextFormatHelper
import com.tw.longerrelationship.util.getComparedTime
import com.tw.longerrelationship.util.gone
import com.tw.longerrelationship.util.visible
import com.tw.longerrelationship.views.activity.DairyInfoActivity
import com.tw.longerrelationship.views.activity.HomeActivity


class DairyAdapter(val context: Context, var type: Int = 1, val isHomeActivity: Boolean = true) :
    PagingDataAdapter<DairyItem, RecyclerView.ViewHolder>(COMPARATOR) {
    private var dairyKey: String? = null
    var checkBoxMap = HashMap<Int, Boolean>()           // 保存checkBox状态
    val checkedNum = MutableLiveData<Int>()
    var isShowBox = false

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

    // TODO 切换RecyclerView的显示方式时，可能会导致图片不显示的问题，暂时没找到解决办法，推测和Recycler缓存有关
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dairyItem = getItem(position) ?: return

        when (holder) {
            is FoldViewHolder -> {
                val content = StringBuilder()
                holder.time.text = getComparedTime(dairyItem.createTime)
                holder.time.visible()
                holder.checkBox.setOnClickListener {
                    setSelectItem(position)
                }
                for (i in dairyItem.uriList.indices){
                    content.append("[图片]")
                }
                if (dairyItem.content != null) {
                    content.append(dairyItem.content)
                }
                if (dairyKey!=null){
                    holder.content.text = Html.fromHtml(TextFormatHelper.formatKeyWordColor(dairyKey!!, content.toString()))
                }else{
                    holder.content.text = content
                }


                if (dairyItem.title?.isNotEmpty() == true) {
                    holder.title.visible()
                    if (dairyKey != null) {
                        holder.title.text =
                            Html.fromHtml(TextFormatHelper.formatKeyWordColor(dairyKey!!, dairyItem.title))
                    } else {
                        holder.title.text = dairyItem.title
                    }
                } else {
                    holder.title.gone()
                }

                if (isShowBox) {
                    if (holder.title.visibility == View.GONE) {
                        holder.time.gone()
                    }
                    holder.checkBox.visible()
                    holder.checkBox.isChecked = checkBoxMap[position]!!
                } else {
                    holder.checkBox.gone()
                }
            }

            is UnfoldViewHolder -> {
                holder.checkBox.setOnClickListener {
                    setSelectItem(position)
                }
                holder.time.text = getComparedTime(dairyItem.createTime)
                if (dairyItem.uriList.isNotEmpty()) {
                    Glide.with(this.context).load(dairyItem.uriList[0])
                        .into(holder.picture)
                } else {
                    holder.picture.visibility = View.GONE
                }

                if (dairyItem.content != null) {
                    holder.content.text = dairyItem.content
                } else {
                    holder.content.text = ""
                }
                if (isShowBox) {
                    holder.checkBox.visibility = View.VISIBLE
                    holder.checkBox.isChecked = checkBoxMap[position]!!
                } else holder.checkBox.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            if (isShowBox) {
                setSelectItem(position)
                when (holder) {
                    is FoldViewHolder -> holder.checkBox.isChecked = checkBoxMap[position]!!
                    is UnfoldViewHolder -> holder.checkBox.isChecked = checkBoxMap[position]!!
                }
            } else {
                (context as Activity).startActivity(
                    Intent(context, DairyInfoActivity::class.java).apply {
                        putExtra("dairyId", dairyItem.id)
                    }
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    private fun setSelectItem(position: Int) {
        checkBoxMap[position] = !checkBoxMap[position]!!
        checkedNum.value = checkedNum.value!!.plus(if (checkBoxMap[position]!!) 1 else -1)
    }

    fun selectAll() {
        for (i in 0 until itemCount) {
            checkBoxMap[i] = true
        }
        checkedNum.value = itemCount
    }

    private fun initMap() {
        checkBoxMap.clear()
        for (i in 0 until itemCount) {
            checkBoxMap[i] = false
        }
        checkedNum.value = 0
    }

    fun getDairyItem(position: Int): DairyItem? {
        return super.getItem(position)
    }

    /** 设置关键字 */
    fun setDairyKey(key: String) {
        if (dairyKey!=key){
            dairyKey = key
            notifyDataSetChanged()
        }
    }

    /**
     * View依附到Window
     */
//    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
//        super.onViewAttachedToWindow(holder)
//        if (holder.itemViewType == 1) {
//            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scroll_to_right))
//        } else {
//            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_in_center))
//        }
//    }


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
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            if (isHomeActivity) {
                itemView.setOnLongClickListener {
                    initMap()
                    setSelectItem(this.layoutPosition)
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(200)
                    }
                    (context as HomeActivity).entryCheckType(true)
                    true
                }
            }
        }
    }

    inner class UnfoldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.iv_photo)
        val content: TextView = itemView.findViewById(R.id.tv_content)
        val time: TextView = itemView.findViewById(R.id.tv_time)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            itemView.setOnLongClickListener {
                initMap()
                setSelectItem(this.layoutPosition)
                (context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(1000)
                (context as HomeActivity).entryCheckType(true)
                true
            }
        }
    }
}