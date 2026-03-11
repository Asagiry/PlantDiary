package com.asagiry.plantdiary.ui.common

import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.PlantType

fun PlantType.labelRes(): Int =
    when (this) {
        PlantType.INDOOR -> R.string.indoor
        PlantType.GARDEN -> R.string.garden
    }

