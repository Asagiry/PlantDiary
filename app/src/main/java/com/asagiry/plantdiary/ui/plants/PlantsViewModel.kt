package com.asagiry.plantdiary.ui.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import com.asagiry.plantdiary.ui.common.handle
import com.asagiry.plantdiary.ui.common.repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlantsViewModel(
    private val repository: com.asagiry.plantdiary.data.repository.PlantDiaryRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow<PlantType?>(null)

    val plants: StateFlow<List<Plant>> =
        combine(query, filter) { search, type -> search to type }
            .flatMapLatest { (search, type) -> repository.observePlants(search, type) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun onFilterChanged(value: PlantType?) {
        filter.value = value
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                PlantsViewModel(
                    repository = repository(),
                )
            }
        }
    }
}
