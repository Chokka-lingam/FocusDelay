package com.focusdelay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.focusdelay.data.AppInfo
import com.focusdelay.data.AppRepository
import com.focusdelay.data.PrefsManager

class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val prefsManager = PrefsManager(application)

    var apps by mutableStateOf(emptyList<AppInfo>())
        private set

    var selectedPackages by mutableStateOf(prefsManager.getSelectedPackages())
        private set

    fun loadApps() {
        apps = repository.loadUserInstalledApps()
    }

    fun onTogglePackage(packageName: String) {
        selectedPackages = selectedPackages.toMutableSet().apply {
            if (contains(packageName)) remove(packageName) else add(packageName)
        }
    }

    fun saveSelection() {
        prefsManager.setSelectedPackages(selectedPackages)
    }
}
