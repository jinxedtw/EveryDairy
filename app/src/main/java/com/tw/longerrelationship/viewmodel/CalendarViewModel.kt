package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.views.activity.DairyInfoActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class CalendarViewModel(val repository: MainRepository) : ViewModel() {

    suspend fun getAllDairyDate() = repository.getAllDate()

    fun getDiaryByDate(day: Long): Flow<PagingData<DairyItem>> {
        return repository.getDiaryByDate(day).cachedIn(viewModelScope)
    }
}