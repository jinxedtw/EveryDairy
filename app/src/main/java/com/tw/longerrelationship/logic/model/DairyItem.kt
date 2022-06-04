package com.tw.longerrelationship.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tw.longerrelationship.R
import java.util.*

@Entity(tableName = "DairyEntity")
data class DairyItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String?,                                      // 日记标题
    val content: String?,                                    // 日记内容
    val createTime: Date,                                    // 创建时间
    val editInfoList: List<Date>,                            // 记录修改时间信息
    val location: String = "无定位信息",                       // 位置信息
    val uriList: List<String> = emptyList(),                 // 图片的Uri信息
    val weather: Int = R.drawable.ic_weather,                // 天气
    val mood: Int = R.drawable.ic_mood,                      // 心情
    val isLove: Boolean = false,                             // 是否收藏
    val recordPath: String = "",                                 // 录音位置
)

/** 图片实体类 */
data class DairyPicture(
    val createTime: Date,                                    // 创建时间
    val uriList: List<String> = emptyList(),                 // 图片的Uri信息
)

/** 收藏实体类 */
data class DairyFavorite(
    val collectionName: String,                               // 收藏集名字
    val collectionList: List<Int> = emptyList()               // 收藏的日记
)