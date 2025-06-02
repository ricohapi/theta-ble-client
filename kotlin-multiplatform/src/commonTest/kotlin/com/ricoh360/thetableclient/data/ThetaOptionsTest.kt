package com.ricoh360.thetableclient.data

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.OptionName
import com.ricoh360.thetableclient.service.data.values.WlanAntennaConfig
import com.ricoh360.thetableclient.service.data.values.WlanFrequency
import com.ricoh360.thetableclient.transferred.CameraOptions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ThetaOptionsTest {
    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    @Test
    fun setValueTest() {
        val values = listOf(
            Pair(OptionName.CameraPower, CameraPower.SILENT_MODE),
            Pair(OptionName.CaptureMode, CaptureMode.IMAGE),
            Pair(OptionName.NetworkType, NetworkType.DIRECT),
            Pair(OptionName.Password, "pass123"),
            Pair(OptionName.Ssid, "ssid_123"),
            Pair(OptionName.Username, "jack"),
            Pair(OptionName.WlanAntennaConfig, WlanAntennaConfig.SISO),
            Pair(OptionName.WlanFrequency, WlanFrequency.GHZ_5_8),
        )
        values.forEach {
            val options = ThetaOptions()
            options.setValue(it.first, it.second)
            assertEquals(options.getValue(it.first), it.second, "option: ${it.first}")
        }
    }

    @Test
    fun toThetaOptionsTest() {
        val cameraOptions = CameraOptions(
            cameraPower = "silentMode",
            captureMode = "image",
            networkType = "AP",
            password = "pass123",
            ssid = "ssid_123",
            username = "jack",
            wlanAntennaConfig = "SISO",
            wlanFrequency = 5.2,
        )
        val thetaOptions = ThetaOptions(cameraOptions)
        assertEquals(thetaOptions.cameraPower, CameraPower.SILENT_MODE)
        assertEquals(thetaOptions.captureMode, CaptureMode.IMAGE)
        assertEquals(thetaOptions.networkType, NetworkType.DIRECT)
        assertEquals(thetaOptions.password, cameraOptions.password)
        assertEquals(thetaOptions.ssid, cameraOptions.ssid)
        assertEquals(thetaOptions.username, cameraOptions.username)
        assertEquals(thetaOptions.wlanAntennaConfig, WlanAntennaConfig.SISO)
        assertEquals(thetaOptions.wlanFrequency, WlanFrequency.GHZ_5_2)
    }

    @Test
    fun toCameraOptionsTest() {
        val thetaOptions = ThetaOptions(
            cameraPower = CameraPower.POWER_SAVING,
            captureMode = CaptureMode.VIDEO,
            networkType = NetworkType.CLIENT,
            password = "pass123",
            ssid = "ssid_123",
            username = "jack",
            wlanAntennaConfig = WlanAntennaConfig.MIMO,
            wlanFrequency = WlanFrequency.GHZ_5,
        )
        val cameraOptions = thetaOptions.toCameraOptions()
        assertEquals(cameraOptions.cameraPower, "powerSaving")
        assertEquals(cameraOptions.captureMode, "video")
        assertEquals(cameraOptions.networkType, "CL")
        assertEquals(cameraOptions.password, thetaOptions.password)
        assertEquals(cameraOptions.ssid, thetaOptions.ssid)
        assertEquals(cameraOptions.username, thetaOptions.username)
        assertEquals(cameraOptions.wlanAntennaConfig, "MIMO")
        assertEquals(cameraOptions.wlanFrequency, 5.0)
    }
}
