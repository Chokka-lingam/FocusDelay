package com.focusdelay.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusdelay.utils.PermissionNavigator
import com.focusdelay.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = viewModel()
            var delaySeconds by remember { mutableIntStateOf(vm.getDelaySeconds()) }

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Focus Delay", style = MaterialTheme.typography.headlineMedium)

                    Button(
                        onClick = { startActivity(Intent(this@MainActivity, AppSelectionActivity::class.java)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Apps")
                    }

                    Button(
                        onClick = {
                            PermissionNavigator.openUsageAccessSettings(this@MainActivity)
                            PermissionNavigator.openAccessibilitySettings(this@MainActivity)
                            PermissionNavigator.openOverlaySettings(this@MainActivity)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Permissions")
                    }

                    Text("Set Delay (default 5 seconds): $delaySeconds s")
                    Slider(
                        value = delaySeconds.toFloat(),
                        onValueChange = {
                            delaySeconds = it.toInt().coerceIn(5, 30)
                            vm.setDelaySeconds(delaySeconds)
                        },
                        valueRange = 5f..30f,
                        steps = 24
                    )
                }
            }
        }
    }
}
