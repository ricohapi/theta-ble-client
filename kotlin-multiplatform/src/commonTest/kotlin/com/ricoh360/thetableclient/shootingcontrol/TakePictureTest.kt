package com.ricoh360.thetableclient.shootingcontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.toBytes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class TakePictureTest {
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
     * call takePicture.
     */
    @Test
    fun normalTest() = runBlocking {
        val deferredComplete = CompletableDeferred<Unit>()
        val deferredWrite = CompletableDeferred<Unit>()
        lateinit var takePictureCollect: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "TAKE_PICTURE") {
                takePictureCollect = collect
            }
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "TAKE_PICTURE") {
                assertEquals(data[0], 1, "takePicture")
                deferredWrite.complete(Unit)
            }
        }

        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        device.shootingControlCommand?.takePicture {
            assertNull(it, "takePicture complete no error")
            deferredComplete.complete(Unit)
        }

        withTimeout(1000) {
            deferredWrite.await()
        }

        assertNotNull(device.deferredTakePicture, "takePicture")
        val collectValue = 0.toByte().toBytes()
        takePictureCollect(collectValue)

        withTimeout(1000) {
            deferredComplete.await()
        }
        assertTrue(true, "takePicture")
    }

    /**
     * call takePicture.
     */
    @Test
    fun normalWithoutCompleteTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        device.shootingControlCommand?.takePicture(null)
        assertNull(device.deferredTakePicture, "takePicture")

        assertTrue(true, "takePicture")
    }

    /**
     * Not connected exception for takePicture call.
     */
    @Test
    fun notConnectTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()
        device.connect()
        val shootingControlCommand = device.shootingControlCommand
        device.disconnect()
        shootingControlCommand?.takePicture {
            assertTrue(
                it!!.message!!.indexOf("Not connected", 0, true) >= 0,
                "exception Not connected"
            )
            deferred.complete(Unit)
        }
        // You have to wait a bit to start the thread of takePicture.
        withTimeout(1000) {
            deferred.await()
        }
        assertTrue(true, "takePicture")
    }

    /**
     * Taking picture exception for takePicture call.
     */
    @Test
    fun takingExceptionTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val deferred = CompletableDeferred<Unit>()
        device.deferredTakePicture = CompletableDeferred()
        device.shootingControlCommand?.takePicture {
            assertTrue(it!!.message!!.indexOf("taking", 0, true) >= 0, "exception taking")
            deferred.complete(Unit)
        }
        withTimeout(10000) {
            deferred.await()
        }
        assertTrue(true, "takePicture")
    }

    /**
     * Write exception for takePicture call.
     */
    @Test
    fun writeExceptionTest() = runBlocking {
        val deferredComplete = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, _ ->
            if (characteristic.name == "TAKE_PICTURE") {
                throw Exception("write")
            }
        }

        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        device.shootingControlCommand?.takePicture {
            assertTrue(it!!.message!!.indexOf("write", 0, true) >= 0, "exception write")
            deferredComplete.complete(Unit)
        }

        withTimeout(1000) {
            deferredComplete.await()
        }
        assertTrue(true, "takePicture")
    }

    /**
     * Timeout for takePicture call.
     */
    @Test
    fun timeoutTest() = runBlocking {
        val deferredComplete = CompletableDeferred<Unit>()
        val deferredWrite = CompletableDeferred<Unit>()
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "TAKE_PICTURE") {
                assertEquals(data[0], 1, "takePicture")
                deferredWrite.complete(Unit)
            }
        }

        val timeout = ThetaBle.Timeout(
            timeoutTakePicture = 1000
        )
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName), timeout)
        device.connect()

        device.shootingControlCommand?.takePicture {
            assertTrue(it!!.message!!.indexOf("timeout", 0, true) >= 0, "exception timeout")
            deferredComplete.complete(Unit)
        }

        withTimeout(1000) {
            deferredWrite.await()
        }

        withTimeout(timeout.timeoutTakePicture.toLong() + 2000) {
            deferredComplete.await()
        }
        assertTrue(true, "takePicture")
    }
}
