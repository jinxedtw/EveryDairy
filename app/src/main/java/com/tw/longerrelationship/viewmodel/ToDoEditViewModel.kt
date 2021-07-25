package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tw.longerrelationship.logic.model.EmergencyLevel
import com.tw.longerrelationship.logic.model.ToDoItem
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.flow.filter
import java.util.*

class ToDoEditViewModel(val repository: MainRepository, val todoId: Int) : ViewModel() {
    val toDoData = repository.getTodoById(todoId).filter { it != null }.asLiveData()
    val todoContent: MutableLiveData<String> = MutableLiveData()
    var contentNum: String = "0/300字"
    var createTime: Calendar? = null
    var changeTime: Calendar = Calendar.getInstance()
    var emergencyLevel: EmergencyLevel = EmergencyLevel.EmptyType

    fun saveToDo() {
        repository.saveToDo(
            ToDoItem(
                if (todoId == -1) null else todoId,
                todoContent.value!!,
                if (createTime == null) Calendar.getInstance() else createTime!!,
                changeTime,
                emergencyLevel,
                false
            )
        )
    }
}
