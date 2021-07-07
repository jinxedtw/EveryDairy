package com.tw.longerrelationship.logic.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tw.longerrelationship.logic.dao.DairyDao
import com.tw.longerrelationship.logic.model.DairyItem
import kotlinx.coroutines.flow.Flow


/**
 * repository层,持有dao对象，实际上的进行网络请求和数据库操作
 */
class MainRepository private constructor(
    private val dairyDao: DairyDao,
) {

    /**
     * 通过日记ID查询数据库,获得日记信息
     */
    fun getDairyById(id: Int) = dairyDao.getDairyById(id)

    fun deleteDairy(id: Int) = dairyDao.deleteDairy(id)

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


    companion object {
        private const val PAGE_SIZE = 50

        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(
            dairyDao: DairyDao,
        ): MainRepository {
            return instance ?: synchronized(this) {
                instance ?: MainRepository(
                    dairyDao
                )
            }
        }

    }
}