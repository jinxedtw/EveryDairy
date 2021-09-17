package com.tw.longerrelationship.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.widget.Toast
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


object NetworkUtil {
    private const val TAG = "NetworkUtil"
    private const val TYPE_NO_NETWORK = 1
    private const val TYPE_WIFI = 2
    private const val TYPE_2G = 3
    private const val TYPE_3G = 4
    private const val TYPE_4G = 5

    fun isNetworkConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val info = getActiveNetworkInfo(context) ?: return false
        return info.isConnected
    }

    fun getNetworkType(context: Context?): Int {
        if (context == null) {
            return TYPE_NO_NETWORK
        }
        val info = getActiveNetworkInfo(context) ?: return TYPE_NO_NETWORK
        return if (!info.isConnected) {
            TYPE_NO_NETWORK
        } else getNetworkType(info)
    }

    fun parseNumericAddress(numericAddress: String?): InetAddress? {
        try {
            val parseNumericAddressMethod: Method =
                InetAddress::class.java.getMethod("parseNumericAddress", String::class.java)
            return parseNumericAddressMethod.invoke(null, numericAddress) as InetAddress
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            logE(TAG, "InetAddress class has no parseNumericAddress method", e)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            logE(TAG, "InetAddress class has no parseNumericAddress method", e)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            logE(TAG, "InetAddress class has no parseNumericAddress method", e)
        }
        return null
    }

    private fun getNetworkType(info: NetworkInfo?): Int {
        if (info == null) {
            return TYPE_NO_NETWORK
        }
        var type = TYPE_NO_NETWORK
        when (info.type) {
            ConnectivityManager.TYPE_WIFI -> type = TYPE_WIFI
            ConnectivityManager.TYPE_MOBILE -> type = getMobileNetType(info.subtype)
            else -> {
            }
        }
        return type
    }

    private fun getMobileNetType(subtype: Int): Int {
        when (subtype) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> return TYPE_2G
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> return TYPE_3G
            TelephonyManager.NETWORK_TYPE_LTE -> return TYPE_4G
        }
        return TYPE_NO_NETWORK
    }

    fun showNetworkUnavailableToast(context: Context) {
        showToast(context, "Network unavailable, please open the network!", Toast.LENGTH_LONG)
    }

    private fun getActiveNetworkInfo(context: Context?): NetworkInfo? {
        if (context == null) {
            return null
        }
        try {
            val connectMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectMgr.activeNetworkInfo
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: NoSuchMethodError) {
            e.printStackTrace()
        }
        return null
    }

    val isVpnUsed: Boolean
        get() {
            try {
                val niList = NetworkInterface.getNetworkInterfaces()
                if (niList != null) {
                    for (netInfo in Collections.list(niList)) {
                        if (!netInfo.isUp || netInfo.interfaceAddresses.size == 0) {
                            continue
                        }
                        if ("tun0" == netInfo.name || "ppp0" == netInfo.name) {
                            return true
                        }
                    }
                }
            } catch (e: Throwable) {
            }
            return false
        }
}