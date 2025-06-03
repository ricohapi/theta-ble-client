package com.ricoh360.thetableclient.cameracontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.CameraError
import com.ricoh360.thetableclient.service.data.values.CaptureStatus
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.ShootingFunction
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetStateTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call getState.
     */
    @Test
    fun getStateTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getState/state.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_STATE)
            jsonText.encodeToByteArray()
        }

        val thetaState = service.getState()
        assertEquals(thetaState.batteryLevel, 0.99F)
        assertEquals(thetaState.captureStatus, CaptureStatus.IDLE)
        assertEquals(thetaState.recordedTime, 1)
        assertEquals(thetaState.recordableTime, 3000)
        assertEquals(thetaState.capturedPictures, 10)
        assertEquals(thetaState.latestFileUrl, "http://192.168.1.1/files/100RICOH/R0010267.MP4")
        assertEquals(thetaState.batteryState, ChargingState.CHARGING)
        assertEquals(thetaState.function, ShootingFunction.NORMAL)
        assertEquals(thetaState.cameraError, listOf(CameraError.NO_MEMORY, CameraError.FILE_NUMBER_OVER))
        assertEquals(thetaState.batteryInsert, true)
        assertEquals(thetaState.boardTemp, 38)
        assertEquals(thetaState.batteryTemp, 43)
    }

    /**
     * Serialization exception.
     */
    @Test
    fun notJsonTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/err_test.txt").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_STATE)
            jsonText.encodeToByteArray()
        }

        try {
            service.getState()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleSerializationException) {
            assertTrue(true, "exception")
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }

    /**
     * Not connected exception.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        // setup not connected
        device.peripheral = null

        try {
            service.getState()
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
     * Read exception.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            service.getState()
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("read", 0, true) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read empty exception.
     */
    @Test
    fun emptyBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            service.getState()
            assertTrue(false, "exception empty")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception empty")
        } catch (e: Throwable) {
            assertTrue(false, "exception empty. ${e.message}")
        }
    }
}
