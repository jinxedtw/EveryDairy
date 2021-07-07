package com.tw.longerrelationship.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.tw.longerrelationship.views.activity.DairyInfoActivity
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * use in [DairyInfoActivity]
 */
class DairyInfoViewModel(private val repository: MainRepository, private val dairyId: Int) :
    ViewModel() {

    var pictureList = ArrayList<Uri>()

    fun getDairy() = repository.getDairyById(dairyId)

    fun deleteDairy() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteDairy(dairyId)
    }
}