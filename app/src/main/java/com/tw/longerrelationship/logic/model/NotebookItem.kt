package com.tw.longerrelationship.logic.model

import androidx.room.Entity

@Entity(tableName = "NotebookEntity")
data class NotebookItem(
    val notebookName: String,
    val notebookImg: Int,
    val notebookCount: Int
)
