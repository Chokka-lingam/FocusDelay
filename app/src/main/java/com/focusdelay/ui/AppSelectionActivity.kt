package com.focusdelay.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusdelay.viewmodel.AppSelectionViewModel

class AppSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppSelectionViewModel = viewModel()

            LaunchedEffect(Unit) {
                vm.loadApps()
            }

            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Select apps to delay", style = MaterialTheme.typography.headlineSmall)

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(vm.apps) { app ->
                            val isChecked = vm.selectedPackages.contains(app.packageName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.onTogglePackage(app.packageName) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { vm.onTogglePackage(app.packageName) }
                                )
                                Column {
                                    Text(app.appName)
                                    Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            vm.saveSelection()
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
