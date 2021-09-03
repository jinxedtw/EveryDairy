package com.tw.longerrelationship.util

/**
 * Constants used throughout the app.
 */
const val DATABASE_NAME = "longer-relationship-db"
const val PICTURE_LIST = "pictureList"          // 图片列表
const val CURRENT_PICTURE = "currentPicture"    // 当前图片
const val IF_CAN_DELETE = "ifCanDelete"         // 是否可以删除图片
const val DAIRY_ID = "dairyId"
const val TODO_ID = "todoId"
const val RECOVER_CONTENT="recover_content"     // 恢复日记内容

object Constants {

    object ItemViewType {
        const val UNKNOWN = 0               //未知类型，使用EmptyViewHolder容错处理
        const val PICTURE_SELECT_TAIL = 1   //图片选择尾部增加的类型
        const val PICTURE_SELECT = 2        //图片选择,一般类型
    }

    const val SHARED_PREFERENCES_NAME: String = "app_preferences"
}
