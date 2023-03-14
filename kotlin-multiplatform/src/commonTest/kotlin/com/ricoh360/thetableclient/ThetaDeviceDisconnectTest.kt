package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import kotlinx.coroutines.*
import kotlin.test.*

class ThetaDeviceDisconnectTest {
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
     * call disconnect.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        assertTrue(device.isConnected())

        val deferred = CompletableDeferred<Unit>()
        MockBlePeripheral.onDisconnect = {
            deferred.complete(Unit)
        }
        device.disconnect()
        withTimeout(100) {
            deferred.await()
        }
        assertFalse(device.isConnected())
        assertTrue(true, "disconnect")
    }

    /**
     * Exception for disconnect call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.disconnect()
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
     * Exception for disconnect call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onDisconnect = {
            throw Exception("disconnect")
        }

        try {
            device.connect()
            device.disconnect()
            assertTrue(false, "exception bluetooth")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("disconnect", 0, true) >= 0, "exception bluetooth")
        } catch (e: Throwable) {
            assertTrue(false, "exception bluetooth. ${e.message}")
        }
    }

    /**
     * Timeout for disconnect call.
     */
    @Test
    fun timeoutTest() = runBlocking {
        val timeoutConnect = TIMEOUT_CONNECT
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onDisconnect = {
            delay(timeoutConnect.toLong() + 100)
        }

        try {
            device.connect()
            device.disconnect()
            assertTrue(false, "exception timeout")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("timeout", 0, true) >= 0, "exception timeout")
        } catch (e: Throwable) {
            assertTrue(false, "exception timeout. ${e.message}")
        }
    }

    /**
     * Call cleanup for disconnect call.
     */
    @Test
    fun cleanupTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val deferred = CompletableDeferred<Unit>()
        device.deferredTakePicture = deferred
        device.disconnect()
        assertNull(device.deferredTakePicture, "cleanup")
    }
}
