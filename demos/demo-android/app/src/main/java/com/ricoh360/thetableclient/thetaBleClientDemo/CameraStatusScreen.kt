package com.ricoh360.thetableclient.thetaBleClientDemo

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ricoh360.thetableclient.service.data.ble.PluginControl
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.PluginPowerStatus
import com.ricoh360.thetableclient.thetaBleClientDemo.ui.theme.ThetaSimpleAndroidAppTheme

/**
 * Camera status screen.
 */
@Composable
fun CameraStatusScreen(
    viewModel: ThetaViewModel,
) {
    val padding = 10.dp

    val infoText: String by viewModel.infoText.observeAsState("")
    val batteryLevel: Int by viewModel.batteryLevel.observeAsState(0)
    val batteryStatus: ChargingState? by viewModel.batteryStatus.observeAsState()
    val cameraPower: CameraPower? by viewModel.cameraPower.observeAsState()
    val pluginControl: PluginControl? by viewModel.pluginControl.observeAsState()

    ThetaSimpleAndroidAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("Camera status")
                })
            },
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Battery level: $batteryLevel")
                    Spacer(modifier = Modifier.size(padding))
                    Button(onClick = {
                        viewModel.updateBatteryLevel()
                    }) {
                        Text("Update")
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Battery status: ${batteryStatus?.name}")
                    Spacer(modifier = Modifier.size(padding))
                    Button(onClick = {
                        viewModel.updateBatteryStatus()
                    }) {
                        Text("Update")
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Camera power: ${cameraPower?.name}")
                        Spacer(modifier = Modifier.size(padding))
                        Button(onClick = {
                            viewModel.updateCameraPower()
                        }) {
                            Text("Update")
                        }
                    }
                    Row {
                        Button(onClick = {
                            viewModel.setCameraPower(CameraPower.OFF)
                        }) {
                            Text("Off")
                        }
                        Spacer(modifier = Modifier.size(padding))
                        Button(onClick = {
                            viewModel.setCameraPower(CameraPower.ON)
                        }) {
                            Text("On")
                        }
                        Spacer(modifier = Modifier.size(padding))
                        Button(onClick = {
                            viewModel.setCameraPower(CameraPower.SLEEP)
                        }) {
                            Text("Sleep")
                        }
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Plugin control: ${pluginControl?.pluginControl?.name}")
                        Spacer(modifier = Modifier.size(padding))
                        Button(onClick = {
                            viewModel.updatePluginControl()
                        }) {
                            Text("Update")
                        }
                    }
                    Row {
                        Button(onClick = {
                            viewModel.setPluginControl(PluginPowerStatus.STOP)
                        }) {
                            Text("Stop")
                        }
                        Spacer(modifier = Modifier.size(padding))
                        Button(onClick = {
                            viewModel.setPluginControl(PluginPowerStatus.RUNNING)
                        }) {
                            Text("Running")
                        }
                    }
                }

                Text(text = "\n$infoText")
            }
        }
    }
}
