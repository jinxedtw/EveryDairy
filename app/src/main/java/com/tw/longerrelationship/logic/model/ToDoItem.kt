package com.tw.longerrelationship.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "ToDoEntity")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val content: String,
    val createTime: Calendar,
    val emergencyLevel: EmergencyLevel,
    val complete: Boolean
)

/**
 * @param level 表示该事项的重要程度,等级越高越重要
 */
enum class EmergencyLevel(val level: Int) {
    EmptyType(0),
    NoImportantNoUrgent(1),
    NoImportantUrgent(2),
    ImportantNoUrgent(3),
    ImportantAndUrgent(4);

    companion object {
        fun newInstanceByLevel(level: Int): EmergencyLevel {
            return when (level) {
                0 -> EmptyType
                1 -> NoImportantNoUrgent
                2 -> NoImportantUrgent
                3 -> ImportantNoUrgent
                4 -> ImportantAndUrgent
                else -> EmptyType
            }
        }

        val converterMap =
            mapOf(
                0 to "默认",
                1 to "不重要不紧急",
                2 to "不重要紧急",
                3 to "重要不紧急",
                4 to "重要紧急"
            )
    }
}