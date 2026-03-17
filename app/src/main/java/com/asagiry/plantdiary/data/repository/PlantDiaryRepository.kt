package com.asagiry.plantdiary.data.repository

import com.asagiry.plantdiary.data.local.dao.CareRecordDao
import com.asagiry.plantdiary.data.local.dao.PlantDao
import com.asagiry.plantdiary.data.local.entity.CareRecord
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import java.time.LocalDate
import kotlinx.coroutines.flow.map

class PlantDiaryRepository(
    private val plantDao: PlantDao,
    private val careRecordDao: CareRecordDao,
) {
    fun observePlants(query: String, filter: PlantType?) =
        (if (filter == null) {
            plantDao.observePlants()
        } else {
            plantDao.observePlantsByType(filter)
        }).map { plants ->
            val normalizedQuery = query.trim()
            if (normalizedQuery.isBlank()) {
                plants
            } else {
                plants.filter { plant -> plant.name.contains(normalizedQuery, ignoreCase = true) }
            }
        }

    fun observeAllPlantChoices() = plantDao.observeAllPlantChoices()

    suspend fun getPlantById(id: Long) = plantDao.getPlantById(id)

    suspend fun getCareRecordCountForPlant(plantId: Long) = careRecordDao.getCountForPlant(plantId)

    suspend fun savePlant(plant: Plant): Long =
        if (plant.id == 0L) {
            plantDao.insertPlant(plant)
        } else {
            plantDao.updatePlant(plant)
            plant.id
        }

    suspend fun deletePlant(plant: Plant) = plantDao.deletePlant(plant)

    fun observeCareRecords() = careRecordDao.observeCareRecords()

    fun observeCareRecordsForWatering(date: LocalDate) = careRecordDao.observeCareRecordsForWatering(date)

    fun observeCareRecordsForPlanting(date: LocalDate) = careRecordDao.observeCareRecordsForPlanting(date)

    suspend fun getCareRecordById(id: Long) = careRecordDao.getCareRecordById(id)

    suspend fun saveCareRecord(careRecord: CareRecord): Long =
        if (careRecord.id == 0L) {
            careRecordDao.insertCareRecord(careRecord)
        } else {
            careRecordDao.updateCareRecord(careRecord)
            careRecord.id
        }

    suspend fun deleteCareRecord(careRecord: CareRecord) = careRecordDao.deleteCareRecord(careRecord)
}
