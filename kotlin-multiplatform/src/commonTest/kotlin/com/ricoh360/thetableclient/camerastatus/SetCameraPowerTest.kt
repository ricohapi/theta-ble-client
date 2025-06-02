package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.CameraPower
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetCameraPowerTest {
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
     * call setCameraPower.
     */
    @Test
    fun normalTest() = runBlocking {
        val valueList = listOf(
            CameraPower.ON,
            CameraPower.OFF,
            CameraPower.SLEEP,
        )
        valueList.forEach {
            setValueTest(it)
        }
    }

    private fun setValueTest(testValue: CameraPower) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "CAMERA_POWER") {
                assertEquals(data[0], testValue.ble, "set value")
                deferred.complete(Unit)
            }
        }
        device.cameraStatusCommand?.setCameraPower(testValue)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setCameraPower")
    }

    /**
     * Not connected exception for setCameraPower call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraStatusCommand = device.cameraStatusCommand
            device.disconnect()
            cameraStatusCommand?.setCameraPower(CameraPower.SLEEP)
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
     * Read exception for setCameraPower call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val testValue = CameraPower.OFF
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "CAMERA_POWER") {
                assertEquals(data[0], testValue.ble, "set value")
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.cameraStatusCommand?.setCameraPower(testValue)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
