package com.tw.longerrelationship.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tw.longerrelationship.MyApplication

/**
 * 获取DataStore实例
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(MyApplication.context.packageName + "_preferences")