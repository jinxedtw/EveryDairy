package com.tw.longerrelationship.logic.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.model.ToDoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Query("SELECT * FROM ToDoEntity WHERE id=:id")
    fun getTodoById(id: Int): Flow<ToDoItem>

    /**
     * 根据id和紧急程度排序,查找所有未完成的待办事项
     */
    @Query("SELECT * FROM ToDoEntity WHERE complete = 0 order by emergencyLevel desc,changeTime desc")
    fun getNotCompleteToDoItem(): DataSource.Factory<Int, ToDoItem>

    @Query("SELECT * FROM ToDoEntity WHERE complete = 1 order by emergencyLevel desc,changeTime desc")
    fun getCompleteToDoItem(): DataSource.Factory<Int, ToDoItem>

    /**
     * 设置待办事项完成
     */
    @Query("UPDATE ToDoEntity SET complete=1 WHERE id=:id")
    fun setTodoComplete(id: Int)

    @Update
    fun updateTodo(toDoItem: ToDoItem)

    @Insert
    fun insertTodo(toDoItem: ToDoItem)

//    /**
//     * 关键字搜索,根据传进来的key值
//     * 用||进行分隔而不是用+
//     */
//    @Query("SELECT * FROM DairyEntity Where content Like '%'||:key|| '%' OR title Like '%'||:key||'%'")
//    fun getKeyDairy(key: String): DataSource.Factory<Int, DairyItem>
}