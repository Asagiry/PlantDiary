package com.asagiry.plantdiary.ui.common

import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.TimeZone

fun Fragment.showDatePicker(onSelected: (LocalDate) -> Unit) {
    val picker = MaterialDatePicker.Builder.datePicker().build()
    picker.addOnPositiveButtonClickListener { utcMillis ->
        val offset = TimeZone.getDefault().getOffset(utcMillis)
        val localDate = Instant.ofEpochMilli(utcMillis + offset)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        onSelected(localDate)
    }
    picker.show(parentFragmentManager, picker.toString())
}

fun Fragment.showTimePicker(onSelected: (LocalTime) -> Unit) {
    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .build()
    picker.addOnPositiveButtonClickListener {
        onSelected(LocalTime.of(picker.hour, picker.minute))
    }
    picker.show(parentFragmentManager, picker.toString())
}

