package com.tw.longerrelationship.logic.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tw.longerrelationship.logic.dao.DairyDao
import com.tw.longerrelationship.logic.dao.ToDoDao
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.model.ToDoItem
import kotlinx.coroutines.flow.Flow


/**
 * repository层,持有dao对象，实际上的进行网络请求和数据库操作
 */
class MainRepository private constructor(
    private val dairyDao: DairyDao,
    private val todoDao: ToDoDao
) {
    // -----------------------------笔记相关接口
    fun getDairyById(id: Int) = dairyDao.getDairyById(id)

    fun deleteDairy(id: Int) = dairyDao.deleteDairy(id)

    fun favoritesDairy(id: Int, ifLove: Boolean) = dairyDao.favoritesDairy(id, ifLove)

    fun saveDairy(dairyItem: DairyItem) =
        if (dairyItem.id == null) dairyDao.insertDairy(dairyItem)
        else dairyDao.updateDairy(dairyItem)

    /**
     * 查找表获得所有日记
     */
    fun getAllDairyData(): Flow<PagingData<DairyItem>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE, maxSize = 150),
            pagingSourceFactory = dairyDao.getAllDairy().asPagingSourceFactory()
        ).flow
    }

    /**
     * 通过关键词搜索日记
     */
    fun getKeyDairyData(key: String): Flow<PagingData<DairyItem>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE, maxSize = 150),
            pagingSourceFactory = dairyDao.getKeyDairy(key).asPagingSourceFactory()
        ).flow
    }

    // ------------------------------待办相关接口
    fun getNotCompleteToDoData(): Flow<PagingData<ToDoItem>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE, maxSize = 150),
            pagingSourceFactory = todoDao.getNotCompleteToDoItem().asPagingSourceFactory()
        ).flow
    }

    fun getCompleteToDoData(): Flow<PagingData<ToDoItem>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE, maxSize = 150),
            pagingSourceFactory = todoDao.getCompleteToDoItem().asPagingSourceFactory()
        ).flow
    }

    fun setTodoComplete(id: Int) = todoDao.setTodoComplete(id)

    fun saveToDo(toDoItem: ToDoItem) = todoDao.insertTodo(toDoItem)


    companion object {
        private const val PAGE_SIZE = 50

        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(dairyDao: DairyDao, toDoDao: ToDoDao): MainRepository {
            return instance ?: synchronized(this) {
                instance ?: MainRepository(dairyDao, toDoDao)
            }
        }
    }
}