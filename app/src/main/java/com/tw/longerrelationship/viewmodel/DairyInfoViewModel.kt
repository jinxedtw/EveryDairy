package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.*
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.util.logV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class DairyInfoViewModel(private val repository: MainRepository, private val dairyId: Int) :
    ViewModel() {

    fun getDairy() = repository.getDairyById(dairyId)


    fun deleteDairy() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteDairy(dairyId)
    }
}