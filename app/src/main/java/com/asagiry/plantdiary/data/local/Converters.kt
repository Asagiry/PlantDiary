package com.asagiry.plantdiary.data.local

import androidx.room.TypeConverter
import com.asagiry.plantdiary.data.local.entity.PlantType
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let(LocalTime::parse)

    @TypeConverter
    fun fromPlantType(value: PlantType): String = value.name

    @TypeConverter
    fun toPlantType(value: String): PlantType = PlantType.valueOf(value)
}

