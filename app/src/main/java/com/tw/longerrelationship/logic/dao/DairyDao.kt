package com.tw.longerrelationship.logic.dao

import androidx.paging.DataSource
import androidx.room.*
import com.tw.longerrelationship.logic.model.DairyItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DairyDao {

    @Query("SELECT * FROM DairyEntity WHERE id=:id")
    fun getDairyById(id: Int): Flow<DairyItem>

    /**
     * 用DataSource把数据绑定到Paging  响应式
     * 根据时间降序查找
     */
    @Query("SELECT * FROM DairyEntity order by createTime desc")
    fun getAllDairy(): DataSource.Factory<Int, DairyItem>

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

    /**
     * 收藏日记
     */
    @Query("UPDATE DairyEntity SET isLove=:isLove Where id=:id")
    fun favoritesDairy(id: Int, isLove: Boolean)
}