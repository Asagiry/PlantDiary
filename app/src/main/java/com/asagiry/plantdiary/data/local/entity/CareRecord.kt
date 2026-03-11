package com.asagiry.plantdiary.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "care_records",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plantId")],
)
data class CareRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val nextWateringDate: LocalDate,
    val plannedPlantingDate: LocalDate?,
    val plannedPlantingTime: LocalTime?,
)

