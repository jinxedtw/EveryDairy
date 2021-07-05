package com.tw.longerrelationship.logic.dao

import androidx.paging.DataSource
import androidx.room.*
import com.tw.longerrelationship.logic.model.DairyItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DairyDao {

    /**
     * 个人理解,返回为Flow的形式
     * 代表数据库在查询的时候在子线程中进行，并且是用flow的数据形式,响应式
     */
    @Query("SELECT * FROM DairyEntity WHERE id=:id")
    fun getDairyById(id: Int): Flow<DairyItem>

    /**
     * 用DataSource把数据绑定到Paging  响应式
     * 根据时间降序查找
     */
    @Query("SELECT * FROM DairyEntity order by time desc")
    fun getAllDairy(): DataSource.Factory<Int, DairyItem>

    /**
     * 根据传入的日记主键来更新已有日记
     */
    @Update
    fun updateDairy(dairyItem: DairyItem)

    /**
     * 关键字搜索,根据传进来的key值
     * 用||进行分隔而不是用+
     */
    @Query("SELECT * FROM DairyEntity Where content Like '%'||:key|| '%' OR title Like '%'||:key||'%'")
    fun getKeyDairy(key: String): DataSource.Factory<Int, DairyItem>

    @Insert
    fun insertDairy(dairyItem: DairyItem)

    @Query("DELETE From DairyEntity Where id=:id")
    fun deleteDairy(id: Int)
}