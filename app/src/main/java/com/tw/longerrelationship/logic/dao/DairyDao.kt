package com.tw.longerrelationship.logic.dao

import androidx.paging.DataSource
import androidx.room.*
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.model.DairyPicture
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface DairyDao {

    @Query("SELECT * FROM DairyEntity WHERE id=:id")
    fun getDairyById(id: Int): Flow<DairyItem>

    @Query("SELECT * FROM DairyEntity order by createTime desc")
    fun getAllDairy(): DataSource.Factory<Int, DairyItem>

    @Update
    fun updateDairy(dairyItem: DairyItem)

    @Query("SELECT * FROM DairyEntity Where content Like '%'||:key|| '%' OR title Like '%'||:key||'%'")
    fun getKeyDairy(key: String): DataSource.Factory<Int, DairyItem>

    @Insert
    fun insertDairy(dairyItem: DairyItem)

    @Query("DELETE From DairyEntity Where id=:id")
    fun deleteDairy(id: Int)

    @Query("UPDATE DairyEntity SET isLove=:isLove Where id=:id")
    fun favoritesDairy(id: Int, isLove: Boolean)

    @Query("SELECT uriList,createTime FROM DairyEntity where uriList is not null and uriList != '' order by createTime desc")
    suspend fun getALlPictures(): List<DairyPicture>

    @Query("SELECT createTime FROM DairyEntity order by createTime desc")
    suspend fun getALlDiaryTime(): List<Date>

    @Query("SELECT * FROM DairyEntity WHERE  (createTime + 28800000) / 86400000  = :dayCount order by createTime desc")
    fun getDiaryByDate(dayCount: Long): DataSource.Factory<Int, DairyItem>
}