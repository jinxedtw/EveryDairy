package com.tw.longerrelationship.logic.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.viewmodel.MainViewModel

/**
 * ViewModelFactory层
 * 把repository组装起来用来生产ViewModel
 */
class MainViewModelFactory(private val repository: MainRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}