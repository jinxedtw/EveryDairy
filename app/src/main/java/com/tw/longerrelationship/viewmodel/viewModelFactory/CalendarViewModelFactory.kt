package com.tw.longerrelationship.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.viewmodel.AlbumViewModel
import com.tw.longerrelationship.viewmodel.CalendarViewModel

class CalendarViewModelFactory(val mainRepository: MainRepository) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CalendarViewModel(mainRepository) as T
    }
}