package com.tw.longerrelationship.util

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tw.longerrelationship.MyApplication.Companion.appContext
import com.tw.longerrelationship.views.widgets.ToastWithImage
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*


/** 跳转到app的程序信息 */
fun toSelfSetting() {
    val mIntent = Intent()
    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
    mIntent.data = Uri.fromParts("package", appContext.packageName, null)
    appContext.startActivity(mIntent)
}

/** 复制文本 */
fun Context.copyText(data: String?) {
    if (data == null) {
        return
    }
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
        setPrimaryClip(ClipData.newPlainText("copy Content", data))
    }
    ToastWithImage.showToast("Copied", true)
}

/** 打电话 */
fun Activity.callPhone(tel: String) {
    if (PhoneNumberUtils.isGlobalPhoneNumber(tel)) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:${tel}")
        intent.data = data
        startActivity(intent)
    } else {
        ToastWithImage.showToast("无效的电话号码", false)
    }
}

/** 发送邮件 */
fun Activity.sendEmail(address: String) {
    val uri = Uri.parse("mailto:${address}")
    val email = arrayOf(address) // 需要注意，email必须以数组形式传入
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    intent.putExtra(Intent.EXTRA_EMAIL, email) // 接收人
    intent.putExtra(Intent.EXTRA_CC, email) // 抄送人
    startActivity(Intent.createChooser(intent, "请选择邮件类应用"))
}

/** 保存图片到相册 */
fun Activity.savePicToAlbum(bitmap: Bitmap?){
    if (bitmap == null){
        ToastWithImage.showToast("保存失败",false)
    }

    runCatching {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            var imageUri: Uri?
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }

            contentResolver.also { resolver ->
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }

            fos?.use { bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, it) }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            contentResolver.update(imageUri!!, contentValues, null, null)
        }else{
            MediaStore.Images.Media.insertImage(this.contentResolver, bitmap, "", "")
        }
    }.onFailure {
        it.printStackTrace()
        ToastWithImage.showToast("保存失败",false)
    }.onSuccess {
        ToastWithImage.showToast("保存成功",true)
    }
}

/** 发送短信 */
fun Activity.sendSMS(phoneNumber: String, message: String?) {
    if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", message)
        startActivity(intent)
    } else {
        ToastWithImage.showToast("无效的电话号码", false)
    }
}

/** 打开wifi界面 */
fun Activity.openWifiSetting() {
    startActivity(Intent(ACTION_WIFI_SETTINGS))
}

/** 打开系统日历 */
fun Activity.openCalendar(
    title: String, description: String, location: String,
    startTime: Long, endTime: Long,
) {
    val intent: Intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
        .putExtra(CalendarContract.Events.TITLE, title)
        .putExtra(CalendarContract.Events.DESCRIPTION, description)
        .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
    startActivity(intent)
}

/** 打开联系人 */
fun Activity.openContact(
    name: String,
    title: String,
    company: String,
    phone: String,
    email: String,
    website: String,
    address: String,
) {
    val contactData = arrayListOf<ContentValues>()

    val websiteRow = ContentValues().apply {
        put(ContactsContract.CommonDataKinds.Website.URL, website)
    }
    contactData.add(websiteRow)

    val phoneRow = ContentValues().apply {
        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
    }
    contactData.add(phoneRow)

    val emailRow = ContentValues().apply {
        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
    }
    contactData.add(emailRow)

    val insertIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
        type = ContactsContract.RawContacts.CONTENT_TYPE
        putExtra(ContactsContract.Intents.Insert.NAME, name)
        putExtra(ContactsContract.Intents.Insert.COMPANY, company)
        putExtra(ContactsContract.Intents.Insert.JOB_TITLE, title)
        putExtra(ContactsContract.Intents.Insert.NOTES, address)
        putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData)
    }
    startActivity(insertIntent)
}

/** 跳转浏览器 */
fun Activity.openBrowser(url: String) {
    var mUrl: String = url
    if (url.startsWith("www.")) {
        mUrl = "http:${url}"
    }

    openIntent(action = Intent.ACTION_VIEW, uri = Uri.parse(mUrl))
}

/** 分享图片 */
fun Activity.shareImage(bitmap: Bitmap?) {
    if (bitmap == null) {
        ToastWithImage.showToast("分享失败", false)
        return
    }

    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            val uri = Uri.parse(
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "IMG" + Calendar.getInstance().time,
                    null
                )
            )
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "title"))
        } catch (e: Exception) {
            e.printStackTrace()
            ToastWithImage.showToast("分享失败", false)
        }
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }
}


/** 分享扫描结果 */
fun Activity.shareText(text: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.type = "text/plain"
    startActivity(Intent.createChooser(intent, "scan Result"))
}


fun Activity.openIntent(action: String? = null, type: String? = null, uri: Uri? = null) {
    Intent()
        .apply {
            action?.let { this.action = it }
            uri?.let { this.data = it }
            type?.let { this.type = it }
        }
        .let { intent ->
            packageManager?.let {
                if (intent.resolveActivity(it) != null)
                    startActivity(intent)
                else
                    ToastWithImage.showToast("无法找到对应Intent", false)
            }
        }
}