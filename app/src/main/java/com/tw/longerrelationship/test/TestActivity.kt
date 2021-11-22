package com.tw.longerrelationship.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.tw.longerrelationship.R
import com.tw.longerrelationship.logic.LiveDataBus
import com.tw.longerrelationship.util.showToast
import java.lang.reflect.Field
import java.lang.reflect.Method

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        LiveDataBus.with("啦啦啦",String::class.java,isStick = true).observe(this){
            showToast(it)
        }

        findViewById<View>(R.id.bt).setOnClickListener {
            LiveDataBus.with("啦啦啦",String::class.java).postValue("发送了数据")
        }
    }

//    private fun hook(observer: Observer<*>) {
//        val liveDataClazz = LiveData::class.java
//        val mapClazz = SafeIterableMap::class.java
//        val getMethod: Method = mapClazz.getDeclaredMethod("get", Any::class.java)
//        getMethod.isAccessible = true
//        val observerField = liveDataClazz.getDeclaredField("mObservers")
//        observerField.isAccessible = true
//
//        val observers = observerField.get(MyLivedata.myValue)
//        val invokeEntry = getMethod.invoke(observers, observer)
//        var boundObserver: Any? = null
//        if (invokeEntry != null && invokeEntry is Map.Entry<*, *>) {
//            boundObserver = invokeEntry.value
//        }
//        if (boundObserver == null) {
//            throw NullPointerException("observerWraper 为空")
//        }
//
//        val observerWrapperClazz: Class<*> = boundObserver.javaClass.superclass
//        val mLastVersion: Field = observerWrapperClazz.getDeclaredField("mLastVersion")
//        mLastVersion.isAccessible = true
//        val mVersion: Field = liveDataClazz.getDeclaredField("mVersion")
//        mVersion.isAccessible = true
//        val mVersionValue: Any = mVersion.get(MyLivedata.myValue)!!
//        mLastVersion.set(boundObserver, mVersionValue)
//    }
}