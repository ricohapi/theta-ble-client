package com.ricoh360.thetableclient.thetaBleClientDemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ricoh360.thetableclient.thetaBleClientDemo.ui.theme.ThetaSimpleAndroidAppTheme

/**
 * Main menu screen.
 */
@Composable
fun MainScreen(
    toCameraStatus: (viewModel: ThetaViewModel) -> Unit,
    toCameraControlCommandV2: (viewModel: ThetaViewModel) -> Unit,
    viewModel: ThetaViewModel,
) {
    val infoText: String by viewModel.infoText.observeAsState("")
    val deviceName: String? by viewModel.deviceName.observeAsState()
    val useUuid: Boolean? by viewModel.useUuid.observeAsState()
    ThetaSimpleAndroidAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(stringResource(id = R.string.app_name))
                })
            },
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Button(onClick = {
                    viewModel.connectWifi()
                }) {
                    Text("Connect Wifi")
                }
                Text(text = "device: $deviceName${if (deviceName != null && useUuid != false) " use uuid" else ""}")
                Button(
                    onClick = {
                        viewModel.scan()
                    },
                    enabled = deviceName != null
                ) {
                    Text("Scan BLE")
                }
                Button(onClick = {
                    viewModel.connect()
                }) {
                    Text("Connect")
                }
                Button(onClick = {
                    viewModel.getInfo()
                }) {
                    Text("Info")
                }
                Button(onClick = {
                    toCameraStatus(viewModel)
                    viewModel.checkCameraStatusCommand()
                }) {
                    Text("Camera Status")
                }
                Button(onClick = {
                    viewModel.takePicture()
                }) {
                    Text("Take Picture")
                }
                Button(onClick = {
                    toCameraControlCommandV2(viewModel)
                    viewModel.checkCameraControlCommandV2()
                }) {
                    Text("Camera Control Command V2")
                }
                Button(onClick = {
                    viewModel.disconnect()
                }) {
                    Text("Disconnect")
                }

                Text(text = "\n$infoText")
            }
        }
    }
}
