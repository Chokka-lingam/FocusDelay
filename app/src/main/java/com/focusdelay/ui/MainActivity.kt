package com.focusdelay.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.focusdelay.service.FocusAccessibilityService
import com.focusdelay.utils.PermissionNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    PermissionScreen()
                }
            }
        }
    }
}

@Composable
fun PermissionScreen() {
    val context = LocalContext.current

    var hasAccessibility by remember { mutableStateOf(false) }
    var hasOverlay by remember { mutableStateOf(false) }
    var hasUsageStats by remember { mutableStateOf(false) }

    fun checkPermissions() {
        hasAccessibility = isAccessibilityServiceEnabled(context)
        hasOverlay = Settings.canDrawOverlays(context)
        hasUsageStats = PermissionNavigator.hasUsageStatsPermission(context)
    }

    // Re-check permissions every time the user returns to the app
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val allPermissionsGranted = hasAccessibility && hasOverlay && hasUsageStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to FocusDelay", style = MaterialTheme.typography.headlineMedium)
        Text("Please grant the following permissions:")
        Spacer(modifier = Modifier.height(24.dp))

        PermissionRow("Accessibility Service", hasAccessibility) { PermissionNavigator.openAccessibilitySettings(context) }
        PermissionRow("Display Over Other Apps", hasOverlay) { PermissionNavigator.openOverlaySettings(context) }
        PermissionRow("Usage Access", hasUsageStats) { PermissionNavigator.openUsageAccessSettings(context) }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { context.startActivity(Intent(context, AppSelectionActivity::class.java)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = allPermissionsGranted
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun PermissionRow(name: String, granted: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name)
        Button(onClick = onClick, enabled = !granted) {
            Text(if (granted) "Granted" else "Grant")
        }
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val serviceName = "${context.packageName}/${FocusAccessibilityService::class.java.canonicalName}"
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains(serviceName, ignoreCase = false) == true
}
