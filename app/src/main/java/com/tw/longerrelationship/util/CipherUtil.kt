package com.tw.longerrelationship.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object CipherUtil {
    /*
     * 加密用的Key 可以用26个字母和数字组成 使用AES-128-CBC加密模式，key需要为16位。
     */

    private const val iv = "NIfb&95GUY86Gfgh"

    /**
     * @Description AES算法加密明文
     * @param data 明文
     * @param key 密钥，长度16
     * @param iv 偏移量，长度16
     * @return 密文
     */
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

    /**
     * @Description AES算法解密密文
     * @param data 密文
     * @param key 密钥，长度16
     * @param iv 偏移量，长度16
     * @return 明文
     */
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

    /**
     * 编码
     * @param byteArray
     * @return
     */
    private fun base64Encode(byteArray: ByteArray?): String {
        return String(Base64.encode(byteArray, Base64.DEFAULT))
    }

    /**
     * 解码
     * @param base64EncodedString
     * @return
     */
    private fun base64Decode(base64EncodedString: String?): ByteArray {
        return Base64.decode(base64EncodedString, Base64.DEFAULT)
    }

    /**
     * 获得AES 16位的加密Key值
     * 对密码进行base64编码，取前16位当作key
     * @param password 密码
     */
    fun getAESKey(password: String): String {
        var key = base64Encode(password.toByteArray())
        while (key.length < 16) {
            key += 0
        }
        return key
    }
}