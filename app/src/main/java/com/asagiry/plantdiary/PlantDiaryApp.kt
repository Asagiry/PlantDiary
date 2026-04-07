package com.asagiry.plantdiary

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.asagiry.plantdiary.data.local.PlantDiaryDatabase
import com.asagiry.plantdiary.data.preferences.AppPreferences
import com.asagiry.plantdiary.data.repository.PlantDiaryRepository

class PlantDiaryApp : Application() {
    val preferences: AppPreferences by lazy { AppPreferences(this) }

    val repository: PlantDiaryRepository by lazy {
        val database = PlantDiaryDatabase.getInstance(this)
        PlantDiaryRepository(
            plantDao = database.plantDao(),
            careRecordDao = database.careRecordDao(),
        )
    }

    override fun onCreate() {
        super.onCreate()
        preferences.getLanguageTag()?.let(::applyLanguage)
    }

    fun hasSelectedLanguage(): Boolean = preferences.hasSelectedLanguage()

    fun saveLanguage(languageTag: String) {
        preferences.setLanguageTag(languageTag)
    }

    fun applyLanguage(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}
