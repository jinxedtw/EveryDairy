/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tw.longerrelationship.logic

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tw.longerrelationship.logic.model.Tag
import com.tw.longerrelationship.util.logV
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableList as MutableList1

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {
    @TypeConverter
    fun dateToDatestamp(date: Date): Long = date.time

    @TypeConverter
    fun datestampToDate(value: Long): Date = Date(value)

    @TypeConverter
    fun stringToTagList(data: String?): List<Tag> {
        val listType = object : TypeToken<List<Tag>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun tagListToString(objectList: List<Tag>): String {
        return Gson().toJson(objectList)
    }

    /**
     * Uri对象的转换方式
     */
    @TypeConverter
    fun stringToUriList(data: String?): List<Uri> {
        val list = ArrayList<Uri>()

        if (data == null) return list

        data.split("$$").forEach {
            if (it != ""){
                list.add(it.toUri())
            }
        }

        return list
    }

    @TypeConverter
    fun uriListToString(objectList: List<Uri>): String {
        val str: StringBuilder = StringBuilder()
        objectList.forEach {
            str.append("$it$$")
        }

        return str.toString()
    }
}
