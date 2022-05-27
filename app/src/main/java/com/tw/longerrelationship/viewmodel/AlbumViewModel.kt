package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.ViewModel
import com.tw.longerrelationship.logic.repository.MainRepository
import com.tw.longerrelationship.views.activity.DairyInfoActivity
import java.util.*

class AlbumViewModel(val repository: MainRepository) : ViewModel() {

    val pictureMap: MutableMap<String, List<String>> = mutableMapOf()

    suspend fun getAllDairyPicture(){
        val pictureList = repository.getPictures()
        for (i in pictureList.indices) {
            if (!pictureMap.containsKey(formatDate(pictureList[i].createTime))) {
                pictureMap[formatDate(pictureList[i].createTime)] = pictureList[i].uriList
            } else {
                pictureMap[formatDate(pictureList[i].createTime)]= pictureMap[formatDate(pictureList[i].createTime)]!!.plus(pictureList[i].uriList)
            }
        }
    }

    private fun formatDate(date: Date): String {
        val calendar = Calendar.getInstance().apply { time = date }
        return "${calendar.get(Calendar.YEAR)}.${calendar.get(Calendar.MONTH) + 1}.${calendar.get(Calendar.DAY_OF_MONTH)}.${
            DairyInfoActivity.timeConverterMap[calendar.get(
                Calendar.DAY_OF_WEEK
            )]
        }"
    }
}