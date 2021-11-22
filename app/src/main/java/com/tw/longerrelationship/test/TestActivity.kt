package com.tw.longerrelationship.views.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tw.longerrelationship.R

class TestActivity : AppCompatActivity() {
    val liveData: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        liveData.
    }
}