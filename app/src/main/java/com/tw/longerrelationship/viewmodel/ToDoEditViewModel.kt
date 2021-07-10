package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tw.longerrelationship.logic.model.EmergencyLevel
import com.tw.longerrelationship.logic.model.ToDoItem
import com.tw.longerrelationship.logic.repository.MainRepository
import java.util.*

class ToDoEditViewModel(val repository: MainRepository, val todoId: Int) : ViewModel() {
    val todoContent: MutableLiveData<String> = MutableLiveData()
    val contentNum: String = "0/300字"
    val createTime: Calendar = Calendar.getInstance()
    var emergencyLevel: EmergencyLevel = EmergencyLevel.EmptyType

    fun saveToDo() {
        repository.saveToDo(ToDoItem(null, todoContent.value!!, createTime, emergencyLevel, false))
    }
}
