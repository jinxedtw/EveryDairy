package com.tw.longerrelationship.adapter

import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tw.longerrelationship.R
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.views.activity.DairyEditActivity
import com.tw.longerrelationship.views.widgets.PictureTypeSelectDialog

class PictureSelectAdapter(
    var pictureList: List<String>,
    private val activity: DairyEditActivity,
    var rippleDrawable: RippleDrawable
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var parent: ViewGroup? = null
    private var isShowDelete: Boolean = false
    var onImageClick: (View) -> Unit = {}
    var onDeleteClick: (Int) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        this.parent = parent

        return if (viewType == PICTURE_SELECT_TAIL)
            TailViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_picture_select_tail, parent, false
                )
            )
        else CustomViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_picture_select, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CustomViewHolder -> {
                Glide.with(activity)
                    .load(pictureList[position])
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(dp2px(5))))
                    .into(holder.picture)

                holder.itemView.tag = position
                holder.itemView.transitionName = "img_${position}"

                if (isShowDelete) {
                    holder.deleteImage.visible()
                }
                holder.deleteImage.setOnClickListener {
                    if (holder.itemView.tag!=null && holder.itemView.tag is Int){
                        onDeleteClick(holder.itemView.tag as Int)
                    }else{
                        onDeleteClick(position)
                    }
                }
            }
            is TailViewHolder -> {
                holder.itemView.background = rippleDrawable
                holder.itemView.alpha = 0.7f
            }
        }
    }

    override fun getItemCount(): Int {
        if (pictureList.size == 9) return 9
        return pictureList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == pictureList.size && position != 9)
            PICTURE_SELECT_TAIL
        else
            PICTURE_SELECT

    }

    fun showDeleteImage() {
        isShowDelete = true
    }

    inner class TailViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val pictureAdd: FrameLayout = itemView.findViewById(R.id.fl_picture_add)

        init {
            pictureAdd.setOnClickListener {
                if (pictureList.size == 9)
                    showToast("最多只能添加9张图片哦(^_^)")
                else
                    PictureTypeSelectDialog(activity).show()
            }
        }
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.iv_picture)
        val deleteImage: ImageView = itemView.findViewById(R.id.iv_delete_image)

        init {
            itemView.setOnClickListener {
                onImageClick.invoke(it)
            }
        }
    }

    companion object {
        const val UNKNOWN = 0               //未知类型，使用EmptyViewHolder容错处理
        const val PICTURE_SELECT_TAIL = 1   //图片选择尾部增加的类型
        const val PICTURE_SELECT = 2        //图片选择,一般类型
    }
}