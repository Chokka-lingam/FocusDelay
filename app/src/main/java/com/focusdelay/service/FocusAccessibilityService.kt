package com.focusdelay.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.focusdelay.data.PrefsManager
import com.focusdelay.ui.OverlayActivity
import com.focusdelay.utils.FocusDelayManager

class FocusAccessibilityService : AccessibilityService() {

    private lateinit var prefsManager: PrefsManager
    private var lastForegroundPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefsManager = PrefsManager(this)
        Log.d("FocusDelayDebug", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        Log.d("FocusDelayDebug", "Got event for package: [$packageName]")

        // Check 1: Was this the package we just intentionally launched from our overlay?
        if (packageName == FocusDelayManager.intentionallyLaunchedPackage) {
            FocusDelayManager.intentionallyLaunchedPackage = null // Consume the flag
            lastForegroundPackage = packageName // This is now the foreground app
            Log.d("FocusDelayDebug", "Intentional launch detected for [$packageName]. State updated.")
            return
        }

        // Check 2: Is this just an in-app navigation event within the same app?
        if (packageName == lastForegroundPackage) {
            Log.d("FocusDelayDebug", "Foreground package unchanged. Ignoring.")
            return
        }

        Log.d("FocusDelayDebug", "Potential foreground package changed from [$lastForegroundPackage] to [$packageName]")

        // Now, we decide if we need to ACT on this new state.

        // Check 3: Is the new package a real, launchable app? Ignore transient system UI events.
        val isLaunchable = packageManager.getLaunchIntentForPackage(packageName) != null
        if (!isLaunchable && packageName != this.packageName) {
            Log.d("FocusDelayDebug", "Ignoring non-launchable package: [$packageName]")
            return
        }

        // Check 4: Is the new app our own?
        if (packageName == this.packageName) {
            Log.d("FocusDelayDebug", "Event is for our own app. Ignoring.")
            return
        }

        // If we reached here, it's a genuine switch to a different, launchable app.
        // NOW it's safe to update our state.
        lastForegroundPackage = packageName
        Log.d("FocusDelayDebug", "Foreground package officially changed to [$packageName]")

        // Check 5: Is the new app in the user's selected list?
        val selectedPackages = prefsManager.getSelectedPackages()
        if (!selectedPackages.contains(packageName)) {
            Log.d("FocusDelayDebug", "Package is not in the selected list. Ignoring.")
            return
        }

        // If all checks pass, it's a valid switch to a selected app. Trigger the delay.
        Log.d("FocusDelayDebug", "SUCCESS: App switch detected. Launching overlay!")
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
