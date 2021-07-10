package com.tw.longerrelationship.logic.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tw.longerrelationship.logic.model.ToDoItem

@Dao
interface ToDoDao {
    /**
     * 根据id和紧急程度排序,查找所有未完成的待办事项
     */
    @Query("SELECT * FROM ToDoEntity WHERE complete = 0 order by emergencyLevel desc,createTime desc")
    fun getNotCompleteToDoItem(): DataSource.Factory<Int, ToDoItem>

    @Query("SELECT * FROM ToDoEntity WHERE complete = 1 order by emergencyLevel desc,createTime desc")
    fun getCompleteToDoItem(): DataSource.Factory<Int, ToDoItem>

    /**
     * 设置待办事项完成
     */
    @Query("UPDATE ToDoEntity SET complete=1 WHERE id=:id")
    fun setTodoComplete(id: Int)

    @Insert
    fun insertTodo(toDoItem: ToDoItem)
}