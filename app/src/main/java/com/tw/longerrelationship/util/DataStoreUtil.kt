package com.tw.longerrelationship.util

import androidx.datastore.preferences.core.*
import com.tw.longerrelationship.MyApplication.Companion.dataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException

object DataStoreUtil {
    private fun readBooleanFlow(key: String, default: Boolean = false): Flow<Boolean> =
        dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            it[booleanPreferencesKey(key)] ?: default
        }

    fun readBooleanData(key: String): Boolean? {
        var value: Boolean? = false
        runBlocking {
            dataStore.data.first {
                value = it[booleanPreferencesKey(key)]
                true
            }
        }
        return value
    }

    private fun readIntFlow(key: String, default: Int = 0): Flow<Int> =
        dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            it[intPreferencesKey(key)] ?: default
        }

    /** 同步获取Int值 */
    fun readIntData(key: String): Int? {
        var value: Int? = 0
        runBlocking {
            dataStore.data.first {
                value = it[intPreferencesKey(key)]
                true
            }
        }
        return value
    }

    fun readStringFlow(key: String, default: String = ""): Flow<String> =
        dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            it[stringPreferencesKey(key)] ?: default
        }

    fun readStringData(key: String, default: String = ""): String {
        var value = ""
        runBlocking {
            dataStore.data.first {
                value = it[stringPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    private fun readFloatFlow(key: String, default: Float = 0f): Flow<Float?> =
        dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            it[floatPreferencesKey(key)]
        }

    fun readFloatData(key: String): Float? {
        var value: Float? = 0f
        runBlocking {
            dataStore.data.first {
                value = it[floatPreferencesKey(key)]
                true
            }
        }
        return value
    }

    fun readLongFlow(key: String, default: Long = 0L): Flow<Long> =
        dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            it[longPreferencesKey(key)] ?: default
        }

    fun readLongData(key: String): Long? {
        var value: Long? = 0L
        runBlocking {
            dataStore.data.first {
                value = it[longPreferencesKey(key)]
                true
            }
        }
        return value
    }

    suspend fun saveBooleanData(key: String, value: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[booleanPreferencesKey(key)] = value
        }
    }

    fun saveSyncBooleanData(key: String, value: Boolean) = runBlocking { saveBooleanData(key, value) }

    private suspend fun saveIntData(key: String, value: Int) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[intPreferencesKey(key)] = value
        }
    }

    fun saveSyncIntData(key: String, value: Int) = runBlocking { saveIntData(key, value) }

    private suspend fun saveStringData(key: String, value: String) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[stringPreferencesKey(key)] = value
        }
    }

    fun saveSyncStringData(key: String, value: String) = runBlocking { saveStringData(key, value) }

    private suspend fun saveFloatData(key: String, value: Float) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[floatPreferencesKey(key)] = value
        }
    }

    fun saveSyncFloatData(key: String, value: Float) = runBlocking { saveFloatData(key, value) }

    private suspend fun saveLongData(key: String, value: Long) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[longPreferencesKey(key)] = value
        }
    }

    fun saveSyncLongData(key: String, value: Long) = runBlocking { saveLongData(key, value) }

    suspend fun <T : Any> removeData(key: String, typeValue: T) {
        dataStore.edit {
            when (typeValue) {
                is Long -> it.remove(longPreferencesKey(key))
                is String -> it.remove(stringPreferencesKey(key))
                is Int -> it.remove(intPreferencesKey(key))
                is Boolean -> it.remove(booleanPreferencesKey(key))
                is Float -> it.remove(floatPreferencesKey(key))
                else -> throw IllegalArgumentException("This type can be delete in DataStore")
            }
        }
    }

    fun <T : Any> removeDataSync(key: String, typeValue: T) {
        runBlocking {
            dataStore.edit {
                when (typeValue) {
                    is Long -> it.remove(longPreferencesKey(key))
                    is String -> it.remove(stringPreferencesKey(key))
                    is Int -> it.remove(intPreferencesKey(key))
                    is Boolean -> it.remove(booleanPreferencesKey(key))
                    is Float -> it.remove(floatPreferencesKey(key))
                    else -> throw IllegalArgumentException("This type can be delete in DataStore")
                }
            }
        }
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

    fun clearSync() {
        runBlocking {
            dataStore.edit {
                it.clear()
            }
        }
    }

    /** 异步的读取数据 */
    @Suppress("UNCHECKED_CAST")
    fun <U> getData(key: String, default: U): Flow<U> {
        return try {
            val data = when (default) {
                is Long -> readLongFlow(key, default)
                is String -> readStringFlow(key, default)
                is Int -> readIntFlow(key, default)
                is Boolean -> readBooleanFlow(key, default)
                is Float -> readFloatFlow(key, default)
                else -> throw IllegalArgumentException("This type can be saved into DataStore")
            }
            data as Flow<U>
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("dataStore  getData error", debugMode = true)
            flow { emit(default) }
        }
    }

    /** 异步的存放数据 */
    suspend fun <U> putData(key: String, value: U) {
        try {
            when (value) {
                is Long -> saveLongData(key, value)
                is String -> saveStringData(key, value)
                is Int -> saveIntData(key, value)
                is Boolean -> saveBooleanData(key, value)
                is Float -> saveFloatData(key, value)
                else -> throw IllegalArgumentException("This type can be saved into DataStore")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("dataStore error", debugMode = true)
        }
    }

    /** 同步读取数据 */
    inline operator fun <reified U> get(key: String): U? {
        return try {
            val data = when (U::class) {
                Int::class -> readIntData(key)
                Long::class -> readLongData(key)
                String::class -> readStringData(key)
                Boolean::class -> readBooleanData(key)
                Float::class -> readFloatData(key)
                else -> throw IllegalArgumentException("This type can be saved into DataStore")
            }
            data as U
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("dataStore  getData error", debugMode = true)
            null
        }
    }

    /** 同步的存放数据 */
    inline operator fun <reified T> set(key: String, value: T) {
        return try {
            when (T::class) {
                Int::class -> saveSyncIntData(key, value as Int)
                Long::class -> saveSyncLongData(key, value as Long)
                String::class -> saveSyncStringData(key, value as String)
                Boolean::class -> saveSyncBooleanData(key, value as Boolean)
                Float::class -> saveSyncFloatData(key, value as Float)
                else -> throw IllegalArgumentException("This type can be saved into DataStore")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("dataStore error", debugMode = true)
        }
    }
}