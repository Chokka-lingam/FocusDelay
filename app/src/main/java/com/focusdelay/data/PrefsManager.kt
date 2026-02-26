package com.focusdelay.data

import android.content.Context

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getSelectedPackages(): Set<String> = prefs.getStringSet(KEY_SELECTED_PACKAGES, emptySet()) ?: emptySet()

    fun setSelectedPackages(packages: Set<String>) {
        prefs.edit().putStringSet(KEY_SELECTED_PACKAGES, packages).apply()
    }

    fun getDelaySeconds(): Int = prefs.getInt(KEY_DELAY_SECONDS, DEFAULT_DELAY_SECONDS)

    fun setDelaySeconds(seconds: Int) {
        prefs.edit().putInt(KEY_DELAY_SECONDS, seconds.coerceIn(5, 30)).apply()
    }

    companion object {
        private const val PREF_NAME = "focus_delay_prefs"
        private const val KEY_SELECTED_PACKAGES = "selected_packages"
        private const val KEY_DELAY_SECONDS = "delay_seconds"
        const val DEFAULT_DELAY_SECONDS = 5
    }
}
