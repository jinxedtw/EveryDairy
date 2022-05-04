package com.tw.longerrelationship.test

import android.annotation.SuppressLint
import com.tw.longerrelationship.util.runTimeLog
import com.tw.longerrelationship.util.runTimePrint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

class Test {
//    companion object {
//        @SuppressLint("SimpleDateFormat")
//        @JvmStatic
//        fun main(args: Array<String>) {
//            GlobalScope.launch {
//                launch {
//                    for (k in 1..3) {
//                        println("I'm not blocked $k")
//                        delay(100)
//                    }
//                }
//                foo().collect { value -> println(value) }
//            }
//            print(intToString(123))
//        }
//    }
}

fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}

fun intToString(value: Int): String {
    var data = value
    val stringBuilder = StringBuilder()
    while (data >= 10) {
        stringBuilder.append(data % 10)
        data /= 10
    }
    stringBuilder.append(data)
    stringBuilder.reverse()
    return stringBuilder.toString()
}

fun foo(): Flow<Int> = flow { // flow builder
    for (i in 1..3) {
//        delay(100) // pretend we are doing something useful here
        emit(i) // emit next value
    }
}

//fun main() = runBlocking {
//    // Launch a concurrent coroutine to check if the main thread is blocked
//    launch(Dispatchers.IO) {
//        for (k in 1..3) {
//            println("I'm not blocked $k")
//            delay(100)
//        }
//    }
//    // Collect the flow
//    foo().collect { value -> println(value) }
//}

fun main() {
    val a1 = intArrayOf(1, 2, 3)
    val a2 = intArrayOf(1, 2, 3)
    println(a1.toString())
    println(a2.toString())
}

suspend fun requestNetwork1(): Int {
    delay(700)
    return 1
}

suspend fun requestNetwork2(): Int {
    delay(800)
    return 2
}

suspend fun requestNetwork3(): Int {
    delay(900)
    return 3
}


val <T : Any> T.kClass: KClass<T>
    get() = javaClass.kotlin

fun getClass() {

}

class ListNode(var `val`: Int) {
    var next: ListNode? = null
}

//fun main() {
//    val test = 0
//    println("Kotlin type: ${test.kClass}")

//    UrlMatchHelper.getMatchURL("www.baidu.com\n" +
//            "\n" +
//            "https://c.runoob.com/front-end/854/\n" +
//            "\n" +
//            "https://blog.csdn.net/cyan20115/article/details/106552487?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1.no_search_link&spm=1001.2101.3001.4242.2")

//}


/** 翻转链表 */
fun ReverseList(head: ListNode?): ListNode? {
    var result: ListNode? = null
    var nowNode = head
    while (nowNode != null) {
        val node = nowNode.next
        nowNode.next = result
        result = nowNode
        nowNode = node
    }

    return result
}

fun maxInWindows(num: IntArray, size: Int): IntArray {
    var result = ArrayList<Int>()
    var maxValue = 0
    if (size == 0 || size > num.size) {
        return result.toIntArray()
    }

    for (i in num.indices) {
        if (i == 0) {
            for (j in 0 until size) {
                if (num[j] > maxValue) {
                    maxValue = num[j]
                }
            }
            result.add(maxValue)
        } else {
            var right = i + size
            if (right > num.size) {
                break
            }
            if (num[right] > maxValue) {
                maxValue = num[right]
                result.add(maxValue)
            } else {
                result.add(maxValue)
            }
        }
    }

    return result.toIntArray()
}

fun maxProduct(arr: DoubleArray): Double {
    var iMax = 1.0
    var iMin = 1.0
    var max = Int.MIN_VALUE.toDouble()
    for (i in arr.indices) {
        if (arr[i] < 0) {
            val tem = iMax
            iMax = iMin
            iMin = tem
        }

        iMax = max(iMax * arr[i], arr[i])
        iMin = min(iMin * arr[i], arr[i])

        max = max(max, iMax)
    }
    return max
}