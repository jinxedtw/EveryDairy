package com.tw.longerrelationship.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.viewmodel.ToDoEditViewModel

class ToDoEditViewModelFactory(
    private val mainRepository: MainRepository,
    private val todoId: Int
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ToDoEditViewModel(mainRepository,todoId) as T
    }
}
