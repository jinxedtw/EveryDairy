package com.tw.longerrelationship.viewmodel

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
    var pictureList = ArrayList<Uri>()
    var time = Date()
    var location: String = "位置信息"
    var weatherIcon: Int = R.drawable.ic_weather
    var moodIcon: Int = R.drawable.ic_mood

    fun saveDairy(title: String) =
        try {
            repository.saveDairy(
                DairyItem(
                    if (dairyId == -1) null else dairyId,
                    title,
                    dairyContent.value,
                    time,
                    location,
                    pictureList,
                    weatherIcon,
                    moodIcon
                )
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }

    @SuppressLint("SimpleDateFormat")
    fun getNowTimeMonthAndHour(): String {
        return SimpleDateFormat("M月dd日 HH:mm").format(time)
    }
}