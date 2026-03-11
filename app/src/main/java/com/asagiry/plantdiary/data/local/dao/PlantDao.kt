package com.asagiry.plantdiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query(
        """
        SELECT * FROM plants
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name COLLATE NOCASE
        """,
    )
    fun observePlants(query: String): Flow<List<Plant>>

    @Query(
        """
        SELECT * FROM plants
        WHERE name LIKE '%' || :query || '%'
        AND type = :type
        ORDER BY name COLLATE NOCASE
        """,
    )
    fun observePlantsByType(query: String, type: PlantType): Flow<List<Plant>>

    @Query("SELECT * FROM plants ORDER BY name COLLATE NOCASE")
    fun observeAllPlantChoices(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    suspend fun getPlantById(id: Long): Plant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant): Long

    @Update
    suspend fun updatePlant(plant: Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)
}

