package com.asagiry.plantdiary.data.preferences

import android.content.Context

class AppPreferences(context: Context) {
    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLanguageTag(): String? = sharedPreferences.getString(KEY_LANGUAGE_TAG, null)

    fun hasSelectedLanguage(): Boolean = !getLanguageTag().isNullOrBlank()

    fun setLanguageTag(languageTag: String) {
        sharedPreferences.edit()
            .putString(KEY_LANGUAGE_TAG, languageTag)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "plant_diary_prefs"
        private const val KEY_LANGUAGE_TAG = "language_tag"
    }
}
