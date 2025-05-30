package com.example.movie.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            updateResourcesLegacy(context, language)
        }
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.locale = locale
        config.setLayoutDirection(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
        return context
    }

    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences("language", Context.MODE_PRIVATE)
            .getString("lang", "en") ?: "en"
    }

    fun saveLanguage(context: Context, lang: String) {
        val editor = context.getSharedPreferences("language", Context.MODE_PRIVATE).edit()
        editor.putString("lang", lang)
        editor.apply()
    }
}
