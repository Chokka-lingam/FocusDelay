package com.focusdelay.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.focusdelay.data.PrefsManager
import com.focusdelay.utils.FocusDelayManager
import kotlinx.coroutines.delay

class OverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PrefsManager(this)
        val packageName = intent.getStringExtra("package_name")

        setContent {
            var secondsLeft by remember { mutableIntStateOf(prefs.getDelaySeconds()) }

            LaunchedEffect(packageName) {
                Log.d("FocusDelayDebug", "OverlayActivity: Starting countdown for $packageName")
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }

                if (packageName != null) {
                    Log.d("FocusDelayDebug", "OverlayActivity: Countdown finished. Intentionally launching $packageName")
                    // Set the package name to be ignored by the service
                    FocusDelayManager.intentionallyLaunchedPackage = packageName
                    packageManager.getLaunchIntentForPackage(packageName)?.let { startActivity(it) }
                } else {
                    Log.d("FocusDelayDebug", "OverlayActivity: Countdown finished but package name is null.")
                }
                finish()
            }

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Take a breath...", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    Text("$secondsLeft", color = Color.White, style = MaterialTheme.typography.displayLarge)
                }
            }
        }
    }
}
