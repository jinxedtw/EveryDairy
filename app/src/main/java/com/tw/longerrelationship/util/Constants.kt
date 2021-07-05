package com.tw.longerrelationship.util

import com.tw.longerrelationship.MyApplication


/**
 * Constants used throughout the app.
 */
const val DATABASE_NAME = "longer-relationship-db"
const val PLANT_DATA_FILENAME = "plants.json"


object Constants {

    object ItemViewType {
        const val UNKNOWN = 0               //未知类型，使用EmptyViewHolder容错处理
        const val PICTURE_SELECT_TAIL = 1   //图片选择尾部增加的类型
        const val PICTURE_SELECT = 2        //图片选择,一般类型
    }

    const val SHARED_PREFERENCES_NAME:String= "app_preferences"
}
