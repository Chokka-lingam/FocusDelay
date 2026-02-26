package com.focusdelay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.focusdelay.data.PrefsManager

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val prefsManager = PrefsManager(application)

    fun getDelaySeconds(): Int = prefsManager.getDelaySeconds()

    fun setDelaySeconds(seconds: Int) {
        prefsManager.setDelaySeconds(seconds)
    }
}
