package com.ricoh360.thetableclient.cameracontrolv2

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SetOptionsTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        println("setup")
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call setCaptureMode.
     */
    @Test
    fun setOptionsTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onWrite = { characteristic, data ->
            assertEquals(characteristic, BleCharacteristic.SET_OPTIONS)
            val jsonString = data.decodeToString()
            assertTrue(
                (jsonString.indexOf("captureMode", 0, true)) >= 0,
                "exception setOptions"
            )
            assertTrue(
                (jsonString.indexOf("image", 0, true)) >= 0,
                "exception setOptions"
            )
            assertTrue(
                (jsonString.indexOf("ssid", 0, true)) >= 0,
                "exception setOptions"
            )
            assertTrue(
                (jsonString.indexOf("ssid_123", 0, true)) >= 0,
                "exception setOptions"
            )
        }
        service.setOptions(
            ThetaOptions(
                captureMode = CaptureMode.IMAGE,
                ssid = "ssid_123"
            )
        )
        assertTrue(true, "setOptions")
    }

    /**
     * Not connected exception for setCaptureMode call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val service = device.cameraControlCommandV2
            assertNotNull(service)
            device.disconnect()
            service.setOptions(ThetaOptions(captureMode = CaptureMode.IMAGE))
            assertTrue(false, "exception Not connected")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                e.message!!.indexOf("Not connected", 0, true) >= 0,
                "exception Not connected"
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception Not connected. ${e.message}")
        }
    }

    /**
     * Write exception for setOptions call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onWrite = { characteristic, _ ->
            assertEquals(characteristic, BleCharacteristic.SET_OPTIONS)
            throw Exception("write")
        }

        try {
            device.connect()
            val service = device.cameraControlCommandV2
            assertNotNull(service)
            service.setOptions(ThetaOptions(captureMode = CaptureMode.IMAGE))
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
