package com.tw.longerrelationship

import android.annotation.SuppressLint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

class Test {
    companion object {
        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        fun main(args: Array<String>) {
            GlobalScope.launch {
                launch {
                    for (k in 1..3) {
                        println("I'm not blocked $k")
                        delay(100)
                    }
                }
                foo().collect { value -> println(value) }
            }

            print("123")
        }
    }
}

fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
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

val <T:Any> T.kClass: KClass<T>
    get() = javaClass.kotlin

fun getClass(){
    
}

fun main() {
    val test = 0
    println("Kotlin type: ${test.kClass}")
}

