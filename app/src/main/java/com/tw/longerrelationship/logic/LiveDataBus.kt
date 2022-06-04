
package com.tw.longerrelationship.logic

import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.reflect.Field
import java.lang.reflect.Method

object LiveDataBus {

    private val busMap: MutableMap<String, BusMutableLivedata<Any>> by lazy { HashMap() }

    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T> with(key: String,isStick: Boolean = false): BusMutableLivedata<T> {
        if (!busMap.containsKey(key)) {
            busMap[key] = BusMutableLivedata(isStick)
        }
        (busMap[key] as BusMutableLivedata<T>).isStick = isStick
        return busMap[key] as BusMutableLivedata<T>
    }

    class BusMutableLivedata<T> private constructor() : MutableLiveData<T>() {
         var isStick: Boolean = false            // 默认取消粘性事件

        constructor(isStick: Boolean) : this() {
            this.isStick = isStick
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)

            if (!isStick) {
                hook(observer)
            }
        }

        private fun hook(observer: Observer<*>) {
            runCatching {
                val liveDataClazz = LiveData::class.java
                val mapClazz = SafeIterableMap::class.java
                val getMethod: Method = mapClazz.getDeclaredMethod("get", Any::class.java)
                getMethod.isAccessible = true
                val observerField = liveDataClazz.getDeclaredField("mObservers")
                observerField.isAccessible = true

                val observers = observerField.get(this)
                val invokeEntry = getMethod.invoke(observers, observer)
                var boundObserver: Any? = null
                if (invokeEntry != null && invokeEntry is Map.Entry<*, *>) {
                    boundObserver = invokeEntry.value
                }
                if (boundObserver == null) {
                    throw NullPointerException("boundObserver 为空")
                }

                val observerWrapperClazz: Class<*> = boundObserver.javaClass.superclass
                val mLastVersionField: Field = observerWrapperClazz.getDeclaredField("mLastVersion")
                mLastVersionField.isAccessible = true
                val mVersionField: Field = liveDataClazz.getDeclaredField("mVersion")
                mVersionField.isAccessible = true
                val mVersionValue: Any = mVersionField.get(this)!!
                mLastVersionField.set(boundObserver, mVersionValue)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}