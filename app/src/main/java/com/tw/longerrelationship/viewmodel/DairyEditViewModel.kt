package com.tw.longerrelationship.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.*
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.repository.MainRepository
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DairyEditViewModel(private val repository: MainRepository, private val dairyId: Int) :
    ViewModel() {

    val dairyItem: LiveData<DairyItem> = liveData {
        repository.getDairyById(dairyId).asLiveData()
    }


    /**
     * 日记是否修改
     */
    val isChanged: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * 日记的主题内容
     */
    val dairyContent: MutableLiveData<String> = MutableLiveData()

    /**
     * 日记的图片列表
     */
    val pictureList = ArrayList<Uri>()

    val time = Date()

    var location: String = "位置信息"


    fun saveDairy(title: String, weatherIcon: Int, moodIcon: Int) =
        try {
            repository.saveDairy(
                DairyItem(
                    null,
                    title, dairyContent.value, time, location, pictureList, weatherIcon, moodIcon
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


//    init {
//        plants = Transformations.switchMap<Int, List<Plant>>(growZoneNumber,
//            Function<Int, LiveData<List<Any?>?>?> { it ->
//                if (it == com.google.samples.apps.sunflower.viewmodels.PlantListViewModel.NO_GROW_ZONE) { // -1 默认 正常查询
//                    Log.d("item", "apply: 如果是 -1 就全部查询")
//                    this@PlantListViewModel.plantRepository.getPlants()
//                } else {
//                    Log.d(
//                        "item",
//                        "apply: 否则就指定查询 并排序   【在 plants.json 里面查询： growZoneNumber\": 9  就明白了】"
//                    )
//                    this@PlantListViewModel.plantRepository.getPlantsWithGrowZoneNumber(it)
//                }
//            })
//    }
}