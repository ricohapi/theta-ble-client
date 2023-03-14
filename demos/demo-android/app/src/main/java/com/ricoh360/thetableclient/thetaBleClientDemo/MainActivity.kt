package com.ricoh360.thetableclient.thetaBleClientDemo

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

const val REQUEST_MULTI_PERMISSIONS = 101
const val APP_UUID = "6BEDD7A3-4E01-4FE4-9DFB-03BFF23ECFD3"
const val PREFERENCES_NAME = "demoBleApp"
const val KEY_LAST_DEVICE_NAME = "lastDeviceName"
const val KEY_LAST_USE_UUID = "lastUseUuid"

class MainActivity : ComponentActivity() {
    private lateinit var sharedViewModel: ThetaViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[ThetaViewModel::class.java]

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "mainScreen") {
                composable("mainScreen") {
                    MainScreen(
                        toCameraStatus = { navController.navigate("cameraStatusScreen") },
                        toCameraControlCommandV2 = { navController.navigate("cameraControlCommandV2") },
                        sharedViewModel,
                    )
                }
                composable("cameraStatusScreen") {
                    CameraStatusScreen(sharedViewModel)
                }
                composable("cameraControlCommandV2") {
                    CameraControlCommandV2Screen(sharedViewModel)
                }
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.route) {
                        "mainScreen" -> {
                            sharedViewModel.cameraControlCommandV2ClearStateNotify()
                        }
                    }
                }
            }
        }

        sharedViewModel.isConnected.observe(this) {
            if (it) {
                saveDevice()
            }
        }

        checkPermissions()

        loadDevice()
    }

    private fun saveDevice() {
        val preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(KEY_LAST_DEVICE_NAME, sharedViewModel.deviceName.value)
        editor.putBoolean(KEY_LAST_USE_UUID, sharedViewModel.useUuid.value ?: true)
        editor.apply()
    }

    private fun loadDevice() {
        val preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        when (val deviceName = preferences.getString(KEY_LAST_DEVICE_NAME, null)) {
            null -> {
                sharedViewModel.setInfoText("Please connect wifi.")
            }

            else -> {
                val useUuid = preferences.getBoolean(KEY_LAST_USE_UUID, true)
                sharedViewModel.setDevice(deviceName, useUuid)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val requestPermissions = mutableListOf<String>()
        // Bluetooth
        if (Build.VERSION.SDK_INT > 30) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        if (requestPermissions.isNotEmpty()) {
            requestPermissions(requestPermissions.toTypedArray(), REQUEST_MULTI_PERMISSIONS)
            return false
        }
        return true
    }
}
