package com.focusdelay.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusdelay.R
import com.focusdelay.data.AppInfo
import com.focusdelay.viewmodel.AppSelectionViewModel

class AppSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppSelectionViewModel = viewModel()
            var delaySeconds by remember { mutableIntStateOf(vm.getDelaySeconds()) }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                vm.loadApps()
            }

            AppSelectionScreen(
                apps = vm.apps,
                selectedPackages = vm.selectedPackages,
                delaySeconds = delaySeconds,
                onTogglePackage = { vm.onTogglePackage(it) },
                onSetDelaySeconds = {
                    delaySeconds = it.coerceIn(5, 30)
                    vm.setDelaySeconds(delaySeconds)
                },
                onClearSelection = { vm.clearSelection() },
                onSaveAndClose = {
                    vm.saveSelection()
                    // Go to home screen
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun AppSelectionScreen(
    apps: List<AppInfo>,
    selectedPackages: Set<String>,
    delaySeconds: Int,
    onTogglePackage: (String) -> Unit,
    onSetDelaySeconds: (Int) -> Unit,
    onClearSelection: () -> Unit,
    onSaveAndClose: () -> Unit
) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(stringResource(id = R.string.app_selection_title), style = MaterialTheme.typography.headlineSmall)

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(apps) { app ->
                        val isChecked = selectedPackages.contains(app.packageName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTogglePackage(app.packageName) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onTogglePackage(app.packageName) }
                            )
                            Column {
                                Text(app.appName)
                                Text(
                                    app.packageName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Text(stringResource(id = R.string.set_delay, delaySeconds))
                Slider(
                    value = delaySeconds.toFloat(),
                    onValueChange = { onSetDelaySeconds(it.toInt()) },
                    valueRange = 5f..30f,
                    steps = 24
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClearSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.clear_saved_apps))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSaveAndClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.save_and_close))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppSelectionScreenPreview() {
    AppSelectionScreen(
        apps = listOf(
            AppInfo("com.google.android.youtube", "YouTube"),
            AppInfo("com.instagram.android", "Instagram"),
            AppInfo("com.facebook.katana", "Facebook")
        ),
        selectedPackages = setOf("com.google.android.youtube"),
        delaySeconds = 10,
        onTogglePackage = {},
        onSetDelaySeconds = {},
        onClearSelection = {},
        onSaveAndClose = {}
    )
}
