package com.asagiry.plantdiary.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.asagiry.plantdiary.data.local.dao.CareRecordDao
import com.asagiry.plantdiary.data.local.dao.PlantDao
import com.asagiry.plantdiary.data.local.entity.CareRecord
import com.asagiry.plantdiary.data.local.entity.Plant

@Database(
    entities = [Plant::class, CareRecord::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class PlantDiaryDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun careRecordDao(): CareRecordDao

    companion object {
        @Volatile
        private var instance: PlantDiaryDatabase? = null

        fun getInstance(context: Context): PlantDiaryDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlantDiaryDatabase::class.java,
                    "plant_diary.db",
                ).build().also { instance = it }
            }
        }
    }
}

