package com.tw.longerrelationship.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "ToDoEntity")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String?,
    val content: String,
    val time: Calendar,
    val emergencyLevel: EmergencyLevel
)

enum class EmergencyLevel(val level: Int) {
    ImportantAndUrgent(1),
    ImportantNoUrgent(2),
    NoImportantUrgent(3),
    NoImportantNoUrgent(4);

    companion object {
        fun newInstanceByLevel(level: Int): EmergencyLevel {
            return when (level) {
                1 -> ImportantAndUrgent
                2 -> ImportantNoUrgent
                3 -> NoImportantUrgent
                4 -> NoImportantNoUrgent
                else -> NoImportantNoUrgent
            }
        }
    }
}