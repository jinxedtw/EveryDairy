package com.tw.longerrelationship.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tw.longerrelationship.MyApplication
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.EmergencyLevel
import com.tw.longerrelationship.logic.model.ToDoItem
import com.tw.longerrelationship.util.*
import com.tw.longerrelationship.views.activity.MainActivity

class TodoAdapter(val context: Context) :
    PagingDataAdapter<ToDoItem, TodoAdapter.ViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: TodoAdapter.ViewHolder, position: Int) {
        val toDoItem = getItem(position) ?: return
        holder.apply {
            textContent.text = toDoItem.content
            emergencyType.text = EmergencyLevel.converterMap[toDoItem.emergencyLevel.level]
            yearMonth.text = getYearAndDay(toDoItem.createTime)
            hour.text = getHourMinuteTime(toDoItem.createTime)

            if (toDoItem.complete) {
                iconComplete.setDrawable(R.drawable.ic_complete)
            }else{
                iconComplete.setDrawable(R.drawable.ic_not_complete)
                iconComplete.setOnClickListener {
                    if (!toDoItem.complete)
                        (context as MainActivity).setTodoComplete(toDoItem.id!!)
                }
            }

            when (toDoItem.emergencyLevel.level) {
                0 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.context,
                        (R.drawable.rip_todo_level0)
                    )
                }
                1 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.context,
                        (R.drawable.rip_todo_level1)
                    )
                    emergencyType.setColorForText(R.color.noImportantAndNoUrgent)
                }
                2 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.context,
                        (R.drawable.rip_todo_level2)
                    )
                    emergencyType.setColorForText(R.color.noImportantAndUrgent)
                }
                3 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.context,
                        (R.drawable.rip_todo_level3)
                    )
                    emergencyType.setColorForText(R.color.importantAndNoUrgent)
                }
                4 -> {
                    itemView.background = ContextCompat.getDrawable(
                        MyApplication.context,
                        (R.drawable.rip_todo_level4)
                    )
                    emergencyType.setColorForText(R.color.importantAndUrgent)
                }
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
        val textContent: TextView = itemView.findViewById(R.id.tv_content)
        val iconComplete: ImageView = itemView.findViewById(R.id.iv_if_complete)
        val emergencyType: TextView = itemView.findViewById(R.id.tv_emergency_type)
        val yearMonth: TextView = itemView.findViewById(R.id.tv_year_and_month)
        val hour: TextView = itemView.findViewById(R.id.tv_hour)

        init {
            itemView.setOnClickListener {
                showToast((context as Activity), "跳转到编辑界面")
            }
        }
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