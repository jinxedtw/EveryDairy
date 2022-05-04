package com.tw.longerrelationship.util

object Constants {
    const val SHARED_PREFERENCES_NAME: String = "app_preferences"
    const val DATABASE_NAME = "longer-relationship-db"

    /** intent传输键 */
    const val INTENT_PICTURE_LIST = "pictureList"
    const val INTENT_CURRENT_PICTURE = "currentPicture"    // 当前图片
    const val INTENT_DAIRY_ID = "dairyId"
    const val INTENT_TODO_ID = "todoId"
    const val INTENT_IF_CAN_DELETE = "ifCanDelete"         // 是否可以删除图片
    const val INTENT_PICTURE_TIME = "pictureTime"            // 图片创建时间
    const val INTENT_IMAGE_URL = "album_image_url"
    const val INTENT_ALBUM_SELECT_IMAGES = "album_select_images"
    const val INTENT_ALBUM_SELECT_NUM = "album_select_num"
    const val INTENT_GESTURE_LOCK_MODE = "gesture_lock_mode"        // 手势锁模式

    /**  dataStore的存储键 */
    const val KEY_DAIRY_SHOW_FOLD = "dairyShowFold"     // 展示方式
    const val KEY_ACCOUNT_SEX = "sex"                   // 性别    0:man 1:woman
    const val KEY_RECOVER_CONTENT = "recoverContent"    // 恢复日记内容
    const val KEY_RECOVER_TITLE = "recoverTitle"        // 恢复日记标题
    const val KEY_DAIRY_PASSWORD = "dairyPassword"
    const val KEY_STICKER_ID = "stickerId"
    const val KEY_GESTURE_LOCK = "gestureLock"
    const val KEY_GESTURE_LOCK_PATH ="gestureLockPath"

    /** liveData */
    const val LIVE_SET_LOCK = "setLock"     // 设置锁
}
