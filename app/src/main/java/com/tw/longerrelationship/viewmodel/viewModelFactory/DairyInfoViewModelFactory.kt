package com.tw.longerrelationship.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.viewmodel.DairyInfoViewModel

class DairyInfoViewModelFactory(private val repository: MainRepository, private val dairyId: Int) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DairyInfoViewModel(repository,dairyId) as T
    }
}