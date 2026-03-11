package com.asagiry.plantdiary.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.asagiry.plantdiary.data.local.entity.CareRecord
import com.asagiry.plantdiary.data.local.entity.Plant

data class CareRecordWithPlant(
    @Embedded val careRecord: CareRecord,
    @Relation(
        parentColumn = "plantId",
        entityColumn = "id",
    )
    val plant: Plant,
)

