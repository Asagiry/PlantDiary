package com.asagiry.plantdiary.ui.common

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateFormats {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun formatDate(date: LocalDate?): String =
        date?.format(dateFormatter).orEmpty()

    fun formatTime(time: LocalTime?): String =
        time?.format(timeFormatter).orEmpty()
}
