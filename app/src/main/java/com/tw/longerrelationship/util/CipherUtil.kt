package com.tw.longerrelationship.util

import android.util.Base64
import com.tw.longerrelationship.util.Constants.KEY_DAIRY_PASSWORD
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object CipherUtil {
    /*
     * 加密用的Key 可以用26个字母和数字组成 使用AES-128-CBC加密模式，key需要为16位。
     */
    private const val iv = "NIfb&95GUY86Gfgh"

    private const val SECRET_STRING = "阿巴阿巴阿巴嘟嘟嘟啦啦啦啦"

    @Throws(Exception::class)
    fun encryptAES(data: String, key: String): String? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val blockSize = cipher.blockSize
            val dataBytes = data.toByteArray()
            var plaintextLength = dataBytes.size
            if (plaintextLength % blockSize != 0) {
                plaintextLength += (blockSize - plaintextLength % blockSize)
            }
            val plaintext = ByteArray(plaintextLength)
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.size)
            val keyspec = SecretKeySpec(key.toByteArray(), "AES")
            val ivspec = IvParameterSpec(iv.toByteArray()) // CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec)
            val encrypted = cipher.doFinal(plaintext)
            base64Encode(encrypted).trim { it <= ' ' } // BASE64做转码。
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Throws(Exception::class)
    fun decryptAES(data: String?, key: String): String? {
        return try {
            val encrypted1 = base64Decode(data) //先用base64解密
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val keyspec = SecretKeySpec(key.toByteArray(), "AES")
            val ivspec = IvParameterSpec(iv.toByteArray())
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec)
            val original = cipher.doFinal(encrypted1)
            val originalString = String(original)
            originalString.trim { it <= ' ' }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun base64Encode(byteArray: ByteArray?): String {
        return String(Base64.encode(byteArray, Base64.DEFAULT))
    }

    private fun base64Decode(base64EncodedString: String?): ByteArray {
        return Base64.decode(base64EncodedString, Base64.DEFAULT)
    }

    fun getAESKey(password: String): String {
        var key = base64Encode(password.toByteArray())
        while (key.length < 16) {
            key += 0
        }

        if (key.length > 16) {
           key =  key.removeRange(16, key.length)
        }
        return key
    }

    fun savePassword(password: String) {
        val key = getAESKey(password)
        DataStoreUtil[KEY_DAIRY_PASSWORD] = encryptAES(SECRET_STRING, key)
    }

    fun verifyPassword(password: String): Boolean {
        val encodeStr = DataStoreUtil[KEY_DAIRY_PASSWORD] ?: ""

        val key = getAESKey(password)
        return if (decryptAES(encodeStr, key).equals(SECRET_STRING)) {
            showToast("登录成功")
            true
        } else {
            false
        }
    }
}