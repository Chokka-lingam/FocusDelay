package com.focusdelay.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.focusdelay.data.PrefsManager
import com.focusdelay.ui.OverlayActivity
import com.focusdelay.utils.FocusDelayManager

class FocusAccessibilityService : AccessibilityService() {

    private lateinit var prefsManager: PrefsManager

    companion object {
        // Tracks the last app that was in the foreground
        var lastForegroundApp: String? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefsManager = PrefsManager(this)
        Log.d("FocusDelayDebug", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We are only interested in window state changes
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        Log.d("FocusDelayDebug", "Got event for package: [$packageName]")

        // If the foreground app hasn't changed, do nothing.
        if (packageName == lastForegroundApp) {
            Log.d("FocusDelayDebug", "Foreground app is unchanged. Ignoring.")
            return
        }
        // Update the last foreground app
        lastForegroundApp = packageName

        // If our app intentionally launched this, ignore it to prevent a loop
        if (FocusDelayManager.isIntentionalLaunch) {
            FocusDelayManager.isIntentionalLaunch = false
            Log.d("FocusDelayDebug", "Intentional launch detected. Ignoring.")
            return
        }

        // Ignore events from our own app
        if (packageName == this.packageName) {
            Log.d("FocusDelayDebug", "Event is from our own app. Ignoring.")
            return
        }

        val selectedPackages = prefsManager.getSelectedPackages()
        // If the app is not in our selected list, ignore it
        if (!selectedPackages.contains(packageName)) {
            Log.d("FocusDelayDebug", "Package [$packageName] is not in the selected list. Ignoring.")
            return
        }

        Log.d("FocusDelayDebug", "SUCCESS: New foreground app is a selected package. Launching overlay!")
        startActivity(
            Intent(this, OverlayActivity::class.java)
                .putExtra("package_name", packageName)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun onInterrupt() {
        Log.d("FocusDelayDebug", "Service interrupted")
    }
}
