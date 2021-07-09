package com.tw.longerrelationship.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.views.activity.DairyInfoActivity
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * use in [DairyInfoActivity]
 */
class DairyInfoViewModel(private val repository: MainRepository, private val dairyId: Int) :
    ViewModel() {
    lateinit var dairyItem:DairyItem                // 缓存当前的日记实体
    var pictureList = ArrayList<Uri>()
    var ifFavorites = MutableLiveData<Boolean>()    // 是否收藏当前日记

    fun getDairy() = repository.getDairyById(dairyId).filter { it != null }   // 不进行刷选会报空指针

    fun deleteDairy() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteDairy(dairyId)
    }

    fun favoriteDairy(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.favoritesDairy(id, ifFavorites.value!!)
    }
}