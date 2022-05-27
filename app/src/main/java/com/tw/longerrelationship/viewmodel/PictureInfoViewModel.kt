package com.tw.longerrelationship.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class PictureInfoViewModel : ViewModel() {

    lateinit var uriList: ArrayList<String>

    var currentPicture: MutableLiveData<Int> = MutableLiveData()

}