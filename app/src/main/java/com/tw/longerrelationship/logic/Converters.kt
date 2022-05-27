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

import androidx.room.TypeConverter
import com.tw.longerrelationship.logic.model.EmergencyLevel
import java.util.*
import kotlin.collections.ArrayList

class Converters {
    @TypeConverter
    fun dateToDatestamp(date: Date): Long = date.time

    @TypeConverter
    fun datestampToDate(value: Long): Date = Date(value)

    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }

    @TypeConverter
    fun stringToDateList(data: String?): List<Date> {
        val list = ArrayList<Date>()

        if (data == null) return list
        data.split("$$").forEach {
            if (it != "") {
                list.add(datestampToDate(it.toLong()))
            }
        }
        return list
    }

    @TypeConverter
    fun dateListToString(objectList: List<Date>): String {
        val str: StringBuilder = StringBuilder()
        objectList.forEach {
            str.append("${dateToDatestamp(it)}$$")
        }

        return str.toString()
    }

    @TypeConverter
    fun stringToUriList(data: String?): List<String> {
        val list = ArrayList<String>()

        if (data == null) return list
        data.split("$$").forEach {
            if (it != "") {
                list.add(it)
            }
        }
        return list
    }

    @TypeConverter
    fun uriListToString(objectList: List<String>): String {
        val str: StringBuilder = StringBuilder()
        objectList.forEach {
            str.append("$it$$")
        }

        return str.toString()
    }

    @TypeConverter
    fun intToEmergencyLevel(level: Int): EmergencyLevel {
        return EmergencyLevel.newInstanceByLevel(level)
    }
    @TypeConverter
    fun emergencyLevelToInt(emergencyLevel: EmergencyLevel): Int {
        return emergencyLevel.level
    }
}
