package com.tw.longerrelationship.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tw.longerrelationship.MyApplication

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(MyApplication.appContext.packageName + "_preferences")