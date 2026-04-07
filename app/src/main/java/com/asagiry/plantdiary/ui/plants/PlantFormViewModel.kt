package com.asagiry.plantdiary.ui.plants

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.PlantDiaryApp
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import com.asagiry.plantdiary.ui.common.DateFormats
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.launch

class PlantFormViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val plantId: Long = savedStateHandle[ARG_PLANT_ID] ?: 0L

    val name = savedStateHandle.getLiveData(KEY_NAME, "")
    val description = savedStateHandle.getLiveData(KEY_DESCRIPTION, "")
    val type = savedStateHandle.getLiveData(KEY_TYPE, PlantType.INDOOR.name)
    val wateringInterval = savedStateHandle.getLiveData(KEY_INTERVAL, "")
    val plantingDate = savedStateHandle.getLiveData<String?>(KEY_PLANTING_DATE, null)
    val plantingTime = savedStateHandle.getLiveData<String?>(KEY_PLANTING_TIME, null)

    val isGarden: LiveData<Boolean> = type.map { currentType -> currentType == PlantType.GARDEN.name }
    val plantingDateDisplay: LiveData<String> = plantingDate.map { value ->
        DateFormats.formatDate(value?.let(LocalDate::parse))
    }
    val plantingTimeDisplay: LiveData<String> = plantingTime.map { value ->
        DateFormats.formatTime(value?.let(LocalTime::parse))
    }

    init {
        val isLoaded = savedStateHandle.get<Boolean>(KEY_LOADED) ?: false
        if (plantId != 0L && !isLoaded) {
            savedStateHandle[KEY_LOADED] = true
            viewModelScope.launch {
                repository.getPlantById(plantId)?.let { plant ->
                    name.value = plant.name
                    description.value = plant.description
                    type.value = plant.type.name
                    wateringInterval.value = plant.wateringIntervalDays.toString()
                    plantingDate.value = plant.plantingDate?.toString()
                    plantingTime.value = plant.plantingTime?.toString()
                }
            }
        }
    }

    fun setType(selected: PlantType) {
        type.value = selected.name
        if (selected == PlantType.INDOOR) {
            plantingDate.value = null
            plantingTime.value = null
        }
    }

    fun setPlantingDate(date: LocalDate) {
        plantingDate.value = date.toString()
    }

    fun setPlantingTime(time: LocalTime) {
        plantingTime.value = time.toString()
    }

    suspend fun savePlant(): Int? {
        val currentName = name.value?.trim().orEmpty()
        val currentDescription = description.value?.trim().orEmpty()
        val interval = wateringInterval.value?.trim().orEmpty().toIntOrNull()
        val selectedType = PlantType.valueOf(type.value ?: PlantType.INDOOR.name)
        val selectedPlantingDate = plantingDate.value?.let(LocalDate::parse)
        val selectedPlantingTime = plantingTime.value?.let(LocalTime::parse)

        if (currentName.isBlank() || currentDescription.isBlank() || interval == null) {
            return R.string.validation_required
        }

        if (interval <= 0) {
            return R.string.validation_positive_interval
        }

        if (selectedType == PlantType.GARDEN && (selectedPlantingDate == null || selectedPlantingTime == null)) {
            return R.string.validation_required
        }

        repository.savePlant(
            Plant(
                id = plantId,
                name = currentName,
                description = currentDescription,
                type = selectedType,
                wateringIntervalDays = interval,
                plantingDate = selectedPlantingDate,
                plantingTime = selectedPlantingTime,
            ),
        )
        return null
    }

    companion object {
        const val ARG_PLANT_ID = "plantId"

        private const val KEY_NAME = "name"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_TYPE = "type"
        private const val KEY_INTERVAL = "interval"
        private const val KEY_PLANTING_DATE = "plantingDate"
        private const val KEY_PLANTING_TIME = "plantingTime"
        private const val KEY_LOADED = "loaded"

        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantDiaryApp
                PlantFormViewModel(
                    repository = app.repository,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
