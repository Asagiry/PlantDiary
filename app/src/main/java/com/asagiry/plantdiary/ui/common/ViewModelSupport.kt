package com.asagiry.plantdiary.ui.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.asagiry.plantdiary.PlantDiaryApp
import com.asagiry.plantdiary.data.repository.PlantDiaryRepository

fun CreationExtras.repository(): PlantDiaryRepository {
    val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantDiaryApp
    return app.repository
}

fun CreationExtras.handle(): SavedStateHandle = createSavedStateHandle()

