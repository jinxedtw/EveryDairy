package com.tw.longerrelationship.logic

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tw.longerrelationship.logic.dao.DairyDao
import com.tw.longerrelationship.logic.dao.ToDoDao
import com.tw.longerrelationship.logic.model.DairyItem
import com.tw.longerrelationship.logic.model.ToDoItem
import com.tw.longerrelationship.util.Constants.DATABASE_NAME

/**
 * App数据库
 */
@Database(
    entities = [DairyItem::class, ToDoItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun dairyDao(): DairyDao
    abstract fun toDoDao(): ToDoDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME)
                .build()
        }
    }
}