package com.asagiry.plantdiary.ui.care

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.ui.common.repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CareRecordsViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
) : ViewModel() {
    val hasPlants: StateFlow<Boolean> =
        repository.observeAllPlantChoices()
            .map { plants -> plants.isNotEmpty() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val careRecords: StateFlow<List<CareRecordWithPlant>> =
        repository.observeCareRecords()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteRecord(item: CareRecordWithPlant) {
        viewModelScope.launch {
            repository.deleteCareRecord(item.careRecord)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                CareRecordsViewModel(
                    repository = repository(),
                )
            }
        }
    }
}
