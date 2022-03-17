package com.tw.longerrelationship.viewmodel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.viewmodel.FavoritesViewModel

class FavoritesViewModelFactory(val mainRepository: MainRepository) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavoritesViewModel(mainRepository) as T
    }
}