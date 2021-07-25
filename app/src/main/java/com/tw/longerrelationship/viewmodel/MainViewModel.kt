package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tw.longerrelationship.views.fragment.NoteFragment
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.model.ToDoItem
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 数据来源由repository负责
 * ViewModel只负责数据的中转,显示到View上面去
 * [NoteFragment]共用同一个viewModel
 */
class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    val dairyNum = MutableLiveData<Int>()                                            // 笔记数量
    val notCompleteTodoNum = MutableLiveData<Int>()                                  // 未完成待办数量
    val completeTodoNum = MutableLiveData<Int>()                                     // 完成的待办数量
    var isFold: MutableLiveData<Boolean> = MutableLiveData()                         // 是否折叠显示
    val ifEnterCheckBoxType: MutableLiveData<Boolean> = MutableLiveData(false)  // 是否进入选择状态
    var tabSelect: Int = 0              // 当前选中的tab  0为笔记  1为待办

    // -----------------------------笔记相关接口
    fun getAllDairy(): Flow<PagingData<DairyItem>> {
        return repository.getAllDairyData().cachedIn(viewModelScope)
    }

    fun deleteDairy(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteDairy(id)
    }


    // ------------------------------待办相关接口
    fun getNotCompleteTodoList(): Flow<PagingData<ToDoItem>> {
        return repository.getNotCompleteToDoData().cachedIn(viewModelScope)
    }

    fun getCompleteTodoList(): Flow<PagingData<ToDoItem>> {
        return repository.getCompleteToDoData().cachedIn(viewModelScope)
    }

    fun setTodoComplete(id: Int) = repository.setTodoComplete(id)

}