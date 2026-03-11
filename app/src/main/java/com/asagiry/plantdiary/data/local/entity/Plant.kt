package com.asagiry.plantdiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val type: PlantType,
    val wateringIntervalDays: Int,
    val plantingDate: LocalDate?,
    val plantingTime: LocalTime?,
)

