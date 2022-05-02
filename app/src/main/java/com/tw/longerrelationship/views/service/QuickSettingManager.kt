package com.tw.longerrelationship.views.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.tw.longerrelationship.views.activity.DairyEditActivity

/**
 * 系统工具栏
 */
@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingManager : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(applicationContext, DairyEditActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityAndCollapse(intent)
    }
}