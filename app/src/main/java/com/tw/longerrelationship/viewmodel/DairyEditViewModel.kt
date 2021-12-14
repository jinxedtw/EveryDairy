package com.tw.longerrelationship.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.*
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.repository.MainRepository
import kotlinx.coroutines.flow.filter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DairyEditViewModel(private val repository: MainRepository, val dairyId: Int) :
    ViewModel() {
    val dairyItem = repository.getDairyById(dairyId).filter {
        it != null
    }.asLiveData()

    val isChanged: MutableLiveData<Boolean> = MutableLiveData(false)
    val dairyContent: MutableLiveData<String> = MutableLiveData()
    var pictureList = ArrayList<String>()
    var editInfoList = emptyList<Date>()
    var createTime = Date()
    var location: String = "位置信息"
    var weatherIcon: Int = R.drawable.ic_weather
    var moodIcon: Int = R.drawable.ic_mood
    var ifLove: Boolean = false

    fun saveDairy(title: String) =
        try {
            repository.saveDairy(
                DairyItem(
                    if (dairyId == -1) null else dairyId,
                    title,
                    dairyContent.value,
                    createTime,
                    editInfoList.plus(Date()),      // 每次操作都会往时间列表中加入当前操作时间
                    location,
                    pictureList,
                    weatherIcon,
                    moodIcon,
                    ifLove
                )
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }

    @SuppressLint("SimpleDateFormat")
    fun getNowTimeMonthAndHour(): String {
        return SimpleDateFormat("M月dd日 HH:mm").format(createTime)
    }
}