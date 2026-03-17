package com.asagiry.plantdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.asagiry.plantdiary.data.local.entity.CareRecord
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface CareRecordDao {
    @Transaction
    @Query("SELECT * FROM care_records ORDER BY nextWateringDate ASC")
    fun observeCareRecords(): Flow<List<CareRecordWithPlant>>

    @Transaction
    @Query("SELECT * FROM care_records WHERE nextWateringDate = :date ORDER BY nextWateringDate ASC")
    fun observeCareRecordsForWatering(date: LocalDate): Flow<List<CareRecordWithPlant>>

    @Transaction
    @Query(
        """
        SELECT * FROM care_records
        WHERE plannedPlantingDate = :date
        ORDER BY plannedPlantingDate ASC
        """,
    )
    fun observeCareRecordsForPlanting(date: LocalDate): Flow<List<CareRecordWithPlant>>

    @Query("SELECT * FROM care_records WHERE id = :id LIMIT 1")
    suspend fun getCareRecordById(id: Long): CareRecord?

    @Query("SELECT COUNT(*) FROM care_records WHERE plantId = :plantId")
    suspend fun getCountForPlant(plantId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareRecord(careRecord: CareRecord): Long

    @Update
    suspend fun updateCareRecord(careRecord: CareRecord)

    @Delete
    suspend fun deleteCareRecord(careRecord: CareRecord)
}
