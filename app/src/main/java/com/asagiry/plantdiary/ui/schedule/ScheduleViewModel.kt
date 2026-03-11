package com.asagiry.plantdiary.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.ui.common.repository
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
) : ViewModel() {
    private val selectedDate = MutableStateFlow(LocalDate.now())

    val date: StateFlow<LocalDate> = selectedDate

    val wateringRecords: StateFlow<List<CareRecordWithPlant>> =
        selectedDate
            .flatMapLatest { repository.observeCareRecordsForWatering(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val plantingRecords: StateFlow<List<CareRecordWithPlant>> =
        selectedDate
            .flatMapLatest { repository.observeCareRecordsForPlanting(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectToday() {
        selectedDate.value = LocalDate.now()
    }

    fun selectTomorrow() {
        selectedDate.value = LocalDate.now().plusDays(1)
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                ScheduleViewModel(
                    repository = repository(),
                )
            }
        }
    }
}
