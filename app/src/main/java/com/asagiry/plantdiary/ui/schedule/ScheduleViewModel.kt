package com.asagiry.plantdiary.ui.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.ui.common.handle
import com.asagiry.plantdiary.ui.common.repository
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val selectedDateValue = savedStateHandle.getStateFlow(KEY_SELECTED_DATE, LocalDate.now().toString())

    val date: StateFlow<LocalDate> =
        selectedDateValue
            .map(LocalDate::parse)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LocalDate.now())

    val wateringRecords: StateFlow<List<CareRecordWithPlant>> =
        date
            .flatMapLatest { repository.observeCareRecordsForWatering(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val plantingRecords: StateFlow<List<CareRecordWithPlant>> =
        date
            .flatMapLatest { repository.observeCareRecordsForPlanting(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectToday() {
        savedStateHandle[KEY_SELECTED_DATE] = LocalDate.now().toString()
    }

    fun selectTomorrow() {
        savedStateHandle[KEY_SELECTED_DATE] = LocalDate.now().plusDays(1).toString()
    }

    fun selectDate(date: LocalDate) {
        savedStateHandle[KEY_SELECTED_DATE] = date.toString()
    }

    companion object {
        private const val KEY_SELECTED_DATE = "selectedDate"

        val Factory = viewModelFactory {
            initializer {
                ScheduleViewModel(
                    repository = repository(),
                    savedStateHandle = handle(),
                )
            }
        }
    }
}
