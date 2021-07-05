package com.tw.longerrelationship.logic.viewModelFactory;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tw.longerrelationship.viewmodel.PictureInfoViewModel

class PictureInfoViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PictureInfoViewModel() as T
    }

}

