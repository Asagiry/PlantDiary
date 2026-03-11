package com.asagiry.plantdiary

import android.app.Application
import com.asagiry.plantdiary.data.local.PlantDiaryDatabase
import com.asagiry.plantdiary.data.repository.PlantDiaryRepository

class PlantDiaryApp : Application() {
    val repository: PlantDiaryRepository by lazy {
        PlantDiaryRepository(
            plantDao = PlantDiaryDatabase.getInstance(this).plantDao(),
            careRecordDao = PlantDiaryDatabase.getInstance(this).careRecordDao(),
        )
    }
}

