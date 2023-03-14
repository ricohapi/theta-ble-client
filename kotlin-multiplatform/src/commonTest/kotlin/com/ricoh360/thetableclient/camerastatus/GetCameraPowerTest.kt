package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.service.data.values.CameraPower
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class GetCameraPowerTest {
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
     * call getCameraPower.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val targetValue = CameraPower.SLEEP
        assertNotNull(targetValue.ble)
        MockBlePeripheral.onRead = {
            assertEquals(it.name, "CAMERA_POWER")
            targetValue.ble.toBytes()
        }
        val result = device.cameraStatusCommand?.getCameraPower()
        assertEquals(targetValue, result, "getCameraPower")
    }

    /**
     * Not connected exception for getCameraPower call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraStatusCommand = device.cameraStatusCommand
            device.disconnect()
            cameraStatusCommand?.getCameraPower()
            assertTrue(false, "exception Not connected")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                e.message!!.indexOf("Not connected", 0, true) >= 0,
                "exception Not connected",
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception Not connected. ${e.message}")
        }
    }

    /**
     * Read exception for getCameraPower call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            device.connect()
            device.cameraStatusCommand?.getCameraPower()
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("read", 0, true) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read empty for getCameraPower call.
     */
    @Test
    fun emptyBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            device.connect()
            device.cameraStatusCommand?.getCameraPower()
            assertTrue(false, "exception empty")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception empty")
        } catch (e: Throwable) {
            assertTrue(false, "exception empty. ${e.message}")
        }
    }

    /**
     * Read unknown for getCameraPower call.
     */
    @Test
    fun unknownBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            255.toByte().toBytes()
        }

        try {
            device.connect()
            device.cameraStatusCommand?.getCameraPower()
            assertTrue(false, "exception unknown")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Unknown value", 0, true) >= 0, "exception unknown")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception unknown")
        } catch (e: Throwable) {
            assertTrue(false, "exception unknown. ${e.message}")
        }
    }
}
