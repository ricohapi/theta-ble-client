package com.ricoh360.thetableclient.shootingcontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class SetCaptureModeTest {
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
    fun normalTest() = runBlocking {
        val captureModeList = listOf(
            CaptureMode.IMAGE,
            CaptureMode.VIDEO,
            CaptureMode.LIVE,
        )
        captureModeList.forEach {
            setCaptureModeTest(it)
        }
    }

    private fun setCaptureModeTest(captureMode: CaptureMode) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        println("setCaptureTest: $captureMode")

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "CAPTURE_MODE") {
                assertEquals(data[0], captureMode.ble, "setCaptureMode")
                deferred.complete(Unit)
            }
        }
        device.shootingControlCommand?.setCaptureMode(captureMode)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setCaptureMode")
    }

    /**
     * Not connected exception for setCaptureMode call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val shootingControlCommand = device.shootingControlCommand
            device.disconnect()
            shootingControlCommand?.setCaptureMode(CaptureMode.LIVE)
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
     * Read exception for setCaptureMode call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val captureMode = CaptureMode.VIDEO
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "CAPTURE_MODE") {
                assertEquals(data[0], captureMode.ble, "setCaptureMode")
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.shootingControlCommand?.setCaptureMode(captureMode)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
