package com.ricoh360.thetableclient.thetaBleClientDemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.ricoh360.thetableclient.thetaBleClientDemo.ui.theme.ThetaSimpleAndroidAppTheme

/**
 * Camera status screen.
 */
@Composable
fun CameraControlCommandV2Screen(
    viewModel: ThetaViewModel,
) {
    val infoText: String by viewModel.infoText.observeAsState("")

    ThetaSimpleAndroidAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("Camera Control Command V2")
                })
            },
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Button(onClick = {
                    viewModel.cameraControlCommandV2GetInfo()
                }) {
                    Text("Get Info")
                }
                Button(onClick = {
                    viewModel.cameraControlCommandV2GetState()
                }) {
                    Text("Get State")
                }
                Button(onClick = {
                    viewModel.cameraControlCommandV2GetState2()
                }) {
                    Text("Get State2")
                }
                Button(onClick = {
                    viewModel.cameraControlCommandV2SetStateNotify()
                }) {
                    Text("Set State Notify")
                }

                Column(modifier = Modifier.fillMaxHeight()) {
                    Text(text = "\n$infoText")
                }
            }
        }
    }
}
