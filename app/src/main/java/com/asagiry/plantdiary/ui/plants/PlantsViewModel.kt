package com.asagiry.plantdiary.ui.plants

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import com.asagiry.plantdiary.ui.common.handle
import com.asagiry.plantdiary.ui.common.repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlantsViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val filterName = savedStateHandle.getStateFlow<String?>(KEY_FILTER, null)

    val currentQuery: StateFlow<String> = query
    val currentFilter: StateFlow<PlantType?> =
        filterName
            .map { value -> value?.let(PlantType::valueOf) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val plants: StateFlow<List<Plant>> =
        combine(query, currentFilter) { search, type -> search to type }
            .flatMapLatest { (search, type) -> repository.observePlants(search, type) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChanged(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    fun onFilterChanged(value: PlantType?) {
        savedStateHandle[KEY_FILTER] = value?.name
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_FILTER = "filter"

        val Factory = viewModelFactory {
            initializer {
                PlantsViewModel(
                    repository = repository(),
                    savedStateHandle = handle(),
                )
            }
        }
    }
}
