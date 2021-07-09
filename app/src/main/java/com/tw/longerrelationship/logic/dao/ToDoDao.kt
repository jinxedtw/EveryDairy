package com.tw.longerrelationship.logic.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tw.longerrelationship.logic.model.ToDoItem

@Dao
interface ToDoDao {
    /**
     * 根据id和紧急程度排序
     */
    @Query("SELECT * FROM ToDoEntity order by emergencyLevel")
    fun getAllToDoItem(): DataSource.Factory<Int, ToDoItem>

    @Insert
    fun insertTodo(toDoItem: ToDoItem)
}