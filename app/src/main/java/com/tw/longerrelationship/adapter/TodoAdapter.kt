package com.tw.longerrelationship.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.EmergencyLevel
import com.tw.longerrelationship.logic.model.ToDoItem
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.util.Constants.INTENT_TODO_ID
import com.tw.longerrelationship.views.activity.HomeActivity
import com.tw.longerrelationship.views.activity.ToDoEditActivity


// TODO: 2021/7/15 只展示5个Item数据的问题
// TODO: 2021/7/25 长按删除待办事项
class TodoAdapter(val context: Context) :
    PagingDataAdapter<ToDoItem, TodoAdapter.ViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: TodoAdapter.ViewHolder, position: Int) {
        val toDoItem = getItem(position) ?: return
        holder.apply {
            textContent.text = toDoItem.content
            emergencyType.text = EmergencyLevel.converterMap[toDoItem.emergencyLevel.level]
            yearMonth.text = getYearAndDay(toDoItem.changeTime)
            hour.text = getHourMinuteTime(toDoItem.changeTime)

            when (toDoItem.emergencyLevel.level) {
                0 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.appContext,
                        (R.drawable.rip_todo_level0)
                    )
                }
                1 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.appContext,
                        (R.drawable.rip_todo_level1)
                    )
                    emergencyType.setColorForText(R.color.noImportantAndNoUrgent)
                }
                2 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.appContext,
                        (R.drawable.rip_todo_level2)
                    )
                    emergencyType.setColorForText(R.color.noImportantAndUrgent)
                }
                3 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.appContext,
                        (R.drawable.rip_todo_level3)
                    )
                    emergencyType.setColorForText(R.color.importantAndNoUrgent)
                }
                4 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.appContext,
                        (R.drawable.rip_todo_level4)
                    )
                    emergencyType.setColorForText(R.color.importantAndUrgent)
                }
            }

            if (toDoItem.complete) {
                iconComplete.setDrawable(R.drawable.ic_complete)
                ctContent.background = ContextCompat.getDrawable(context, R.drawable.rip_complete_dairy)
                textContent.paintFlags = textContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                R.color.completeText.also {
                    textContent.setColorForText(it)
                    yearMonth.setColorForText(it)
                    hour.setColorForText(it)
                    emergencyType.setColorForText(it)
                }
            } else {
                iconComplete.setDrawable(R.drawable.ic_not_complete)
                iconComplete.setOnClickListener {
                    if (!toDoItem.complete)
                        (context as HomeActivity).setTodoComplete(toDoItem.id!!)
                }
                itemView.setOnClickListener {
                    val intent = Intent(context, ToDoEditActivity::class.java)
                    intent.putExtra(INTENT_TODO_ID, toDoItem.id)
                    (context as HomeActivity).startActivity(intent)
                }
                ctContent.background = ContextCompat.getDrawable(context, R.drawable.rip_dairy)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_todo, parent, false)
        )
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ctContent: ConstraintLayout = itemView.findViewById(R.id.ct_content)
        val textContent: TextView = itemView.findViewById(R.id.tv_content)
        val iconComplete: ImageView = itemView.findViewById(R.id.iv_if_complete)
        val emergencyType: TextView = itemView.findViewById(R.id.tv_emergency_type)
        val yearMonth: TextView = itemView.findViewById(R.id.tv_year_and_month)
        val hour: TextView = itemView.findViewById(R.id.tv_hour)
    }


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ToDoItem>() {
            override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}