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
        val newSelection = selectedPackages.toMutableSet()
        if (newSelection.contains(packageName)) {
            newSelection.remove(packageName)
        } else {
            newSelection.add(packageName)
        }
        selectedPackages = newSelection
    }

    fun saveSelection() {
        prefsManager.setSelectedPackages(selectedPackages)
    }

    fun clearSelection() {
        selectedPackages = emptySet()
        prefsManager.setSelectedPackages(emptySet())
    }

    fun getDelaySeconds(): Int {
        return prefsManager.getDelaySeconds()
    }

    fun setDelaySeconds(seconds: Int) {
        prefsManager.setDelaySeconds(seconds)
    }
}
