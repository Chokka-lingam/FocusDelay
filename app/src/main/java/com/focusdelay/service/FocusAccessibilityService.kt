package com.focusdelay.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import com.focusdelay.data.PrefsManager
import com.focusdelay.ui.OverlayActivity

class FocusAccessibilityService : AccessibilityService() {

    private lateinit var prefsManager: PrefsManager
    private var lastOverlayLaunchTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefsManager = PrefsManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == this.packageName) return

        val selectedPackages = prefsManager.getSelectedPackages()
        if (!selectedPackages.contains(packageName)) return

        val now = SystemClock.elapsedRealtime()
        if (now - lastOverlayLaunchTime < 1500) return
        lastOverlayLaunchTime = now

        startActivity(
            Intent(this, OverlayActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun onInterrupt() = Unit
}
