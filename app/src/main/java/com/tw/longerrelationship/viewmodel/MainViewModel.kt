package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.flow.Flow

/**
 * 数据来源由repository负责
 * ViewModel只负责数据的中转,显示到View上面去
 */
class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    fun getAllDairy(): Flow<PagingData<DairyItem>> {
        return repository.getAllDairyData().cachedIn(viewModelScope)
    }

}