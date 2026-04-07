package com.asagiry.plantdiary.ui.care

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.PlantDiaryApp
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.CareRecord
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import com.asagiry.plantdiary.ui.common.DateFormats
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.launch

class CareFormViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val careRecordId: Long = savedStateHandle[ARG_CARE_RECORD_ID] ?: 0L

    val selectedPlantId = savedStateHandle.getLiveData(KEY_SELECTED_PLANT_ID, 0L)
    val nextWateringDate = savedStateHandle.getLiveData<String?>(KEY_NEXT_WATERING_DATE, null)
    val plannedPlantingDate = savedStateHandle.getLiveData<String?>(KEY_PLANNED_PLANTING_DATE, null)
    val plannedPlantingTime = savedStateHandle.getLiveData<String?>(KEY_PLANNED_PLANTING_TIME, null)

    private val currentPlant = MutableLiveData<Plant?>(null)

    val plantChoices = repository.observeAllPlantChoices().asLiveData()
    val isGardenPlant: LiveData<Boolean> = currentPlant.map { plant ->
        plant?.type == PlantType.GARDEN
    }
    val nextWateringDisplay: LiveData<String> = nextWateringDate.map { value ->
        DateFormats.formatDate(value?.let(LocalDate::parse))
    }
    val plantingDateDisplay: LiveData<String> = plannedPlantingDate.map { value ->
        DateFormats.formatDate(value?.let(LocalDate::parse))
    }
    val plantingTimeDisplay: LiveData<String> = plannedPlantingTime.map { value ->
        DateFormats.formatTime(value?.let(LocalTime::parse))
    }

    init {
        val isLoaded = savedStateHandle.get<Boolean>(KEY_LOADED) ?: false
        if (careRecordId != 0L && !isLoaded) {
            savedStateHandle[KEY_LOADED] = true
            viewModelScope.launch {
                repository.getCareRecordById(careRecordId)?.let { record ->
                    selectedPlantId.value = record.plantId
                    nextWateringDate.value = record.nextWateringDate.toString()
                    plannedPlantingDate.value = record.plannedPlantingDate?.toString()
                    plannedPlantingTime.value = record.plannedPlantingTime?.toString()
                    updateSelectedPlant(record.plantId, syncPlantingFromPlant = false)
                }
            }
        } else {
            val restoredPlantId = selectedPlantId.value ?: 0L
            if (restoredPlantId != 0L) {
                viewModelScope.launch {
                    updateSelectedPlant(restoredPlantId, syncPlantingFromPlant = false)
                }
            }
        }
    }

    fun onPlantSelected(plantId: Long) {
        selectedPlantId.value = plantId
        viewModelScope.launch {
            updateSelectedPlant(plantId, syncPlantingFromPlant = true)
        }
    }

    fun setNextWateringDate(date: LocalDate) {
        nextWateringDate.value = date.toString()
    }

    fun setPlantingDate(date: LocalDate) {
        plannedPlantingDate.value = date.toString()
    }

    fun setPlantingTime(time: LocalTime) {
        plannedPlantingTime.value = time.toString()
    }

    suspend fun saveCareRecord(): Int? {
        val plantId = selectedPlantId.value ?: 0L
        val watering = nextWateringDate.value?.let(LocalDate::parse)
        val plantingDate = plannedPlantingDate.value?.let(LocalDate::parse)
        val plantingTime = plannedPlantingTime.value?.let(LocalTime::parse)
        val plant = repository.getPlantById(plantId)

        if (plant == null || watering == null) {
            return R.string.validation_required
        }

        val requiresPlanting = plant.type == PlantType.GARDEN
        if (requiresPlanting && (plantingDate == null || plantingTime == null)) {
            return R.string.validation_required
        }

        repository.saveCareRecord(
            CareRecord(
                id = careRecordId,
                plantId = plantId,
                nextWateringDate = watering,
                plannedPlantingDate = if (requiresPlanting) plantingDate else null,
                plannedPlantingTime = if (requiresPlanting) plantingTime else null,
            ),
        )
        return null
    }

    private suspend fun updateSelectedPlant(plantId: Long, syncPlantingFromPlant: Boolean) {
        val plant = if (plantId == 0L) null else repository.getPlantById(plantId)
        currentPlant.postValue(plant)
        if (plant?.type != PlantType.GARDEN) {
            plannedPlantingDate.postValue(null)
            plannedPlantingTime.postValue(null)
        } else if (syncPlantingFromPlant) {
            // When the user picks another garden plant, reuse its planting data as a starting point.
            plannedPlantingDate.postValue(plant.plantingDate?.toString())
            plannedPlantingTime.postValue(plant.plantingTime?.toString())
        }
    }

    companion object {
        const val ARG_CARE_RECORD_ID = "careRecordId"

        private const val KEY_SELECTED_PLANT_ID = "selectedPlantId"
        private const val KEY_NEXT_WATERING_DATE = "nextWateringDate"
        private const val KEY_PLANNED_PLANTING_DATE = "plannedPlantingDate"
        private const val KEY_PLANNED_PLANTING_TIME = "plannedPlantingTime"
        private const val KEY_LOADED = "loaded"

        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantDiaryApp
                CareFormViewModel(
                    repository = app.repository,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
