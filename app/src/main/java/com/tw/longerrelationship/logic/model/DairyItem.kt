package com.tw.longerrelationship.logic.model

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.tw.longerrelationship.R
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

@Entity(tableName = "DairyEntity")
data class DairyItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String?,             // 日记标题
    val content: String?,           // 日记内容
    val createTime: Date,           // 创建时间
    val editInfoList: List<Date>,   // 记录修改时间信息
    val location: String,           // 位置信息
    val uriList: List<Uri>,         // 图片的Uri信息
    val weather: Int,               // 天气
    val mood: Int,                  // 心情
    val isLove: Boolean             // 是否收藏
)