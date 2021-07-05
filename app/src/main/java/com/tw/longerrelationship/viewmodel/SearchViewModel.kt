package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.flow.Flow

class SearchViewModel(val repository: MainRepository) : ViewModel() {


    fun getKeyDairy(key: String): Flow<PagingData<DairyItem>> {
        return repository.getKeyDairyData(key).cachedIn(viewModelScope)
    }
}
