package com.tw.longerrelationship.util

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.telephony.PhoneNumberUtils
import androidx.fragment.app.Fragment
import com.tw.longerrelationship.MyApplication.Companion.appContext
import com.tw.longerrelationship.views.widgets.ToastWithImage
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
    val intent = Intent(Intent.ACTION_DIAL)
    val data = Uri.parse("tel:${tel}")
    intent.data = data
    startActivity(intent)
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
fun Activity.savePicToAlbum(bitmap: Bitmap?) {
    if (bitmap != null) {
        MediaStore.Images.Media.insertImage(this.contentResolver, bitmap, "title", "description")
        ToastWithImage.showToast("Saved", true)
    }
}

/** 发送短信 */
fun Activity.sendSMS(phoneNumber: String, message: String?) {
    if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", message)
        startActivity(intent)
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

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))
    startActivity(intent)
}

/** 分享图片 */
fun Activity.shareImage(bitmap: Bitmap?) {
    if (bitmap == null) return

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
}


/** 分享扫描结果 */
fun Activity.shareText(text: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.type = "text/plain"
    startActivity(Intent.createChooser(intent, "scan Result"))
}