package com.asagiry.plantdiary

import android.app.Application
import com.asagiry.plantdiary.data.local.PlantDiaryDatabase
import com.asagiry.plantdiary.data.repository.PlantDiaryRepository

class PlantDiaryApp : Application() {
    val repository: PlantDiaryRepository by lazy {
        val database = PlantDiaryDatabase.getInstance(this)
        PlantDiaryRepository(
            plantDao = database.plantDao(),
            careRecordDao = database.careRecordDao(),
        )
    }
}
