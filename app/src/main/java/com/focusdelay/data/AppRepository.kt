package com.focusdelay.data

import android.content.Context
import android.content.pm.ApplicationInfo

class AppRepository(private val context: Context) {

    fun loadUserInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        return pm.getInstalledApplications(0)
            .asSequence()
            .filter { app ->
                val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                !isSystem && app.packageName != context.packageName
            }
            .map { app ->
                AppInfo(
                    packageName = app.packageName,
                    appName = pm.getApplicationLabel(app).toString()
                )
            }
            .sortedBy { it.appName.lowercase() }
            .toList()
    }
}
