package com.asagiry.plantdiary.ui.common

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormats {
    private val russianLocale = Locale("ru", "RU")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", russianLocale)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", russianLocale)

    fun formatDate(date: LocalDate?): String =
        date?.format(dateFormatter).orEmpty()

    fun formatTime(time: LocalTime?): String =
        time?.format(timeFormatter).orEmpty()
}
