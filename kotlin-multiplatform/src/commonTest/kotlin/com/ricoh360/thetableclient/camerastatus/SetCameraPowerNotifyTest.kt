package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.toBytes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetCameraPowerNotifyTest {
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
     * call setCameraPowerNotify.
     */
    @Test
    fun normalTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        val testValue = CameraPower.ON
        assertNotNull(testValue.ble)

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "CAMERA_POWER") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setCameraPowerNotify { value, error ->
            assertEquals(value, testValue)
            assertNull(error)
            deferred.complete(Unit)
        }
        val data = testValue.ble.toBytes()
        observer(data)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setCameraPowerNotify")
    }

    /**
     * Set empty callback to setCameraPowerNotify.
     */
    @Test
    fun setEmptyTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        var deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        val testValue = CameraPower.OFF
        assertNotNull(testValue.ble)

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "CAMERA_POWER") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setCameraPowerNotify { value, error ->
            assertEquals(value, testValue)
            assertNull(error)
            deferred.complete(Unit)
        }
        val data = testValue.ble.toBytes()
        observer(data)
        withTimeout(100) {
            deferred.await()
        }

        deferred = CompletableDeferred()
        device.cameraStatusCommand?.setCameraPowerNotify(null)

        assertNull(device.observeManager!!.notifyList[BleCharacteristic.CAMERA_POWER])

        observer(data)
        try {
            withTimeout(100) {
                deferred.await()
            }
            assertTrue(false, "call success")
        } catch (e: TimeoutCancellationException) {
            assertTrue(true, "timeout exception")
        } catch (e: Throwable) {
            assertTrue(false, "other exception")
        }
    }

    /**
     * Not connected exception for setCameraPowerNotify call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraStatusCommand = device.cameraStatusCommand
            device.disconnect()
            cameraStatusCommand?.setCameraPowerNotify(null)
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
     * Not supported characteristic exception for setCameraPowerNotify call.
     */
    @Test
    fun notSupportedTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        MockBlePeripheral.onContain = { characteristic ->
            characteristic.name != "CAMERA_POWER"
        }

        device.connect()

        try {
            device.cameraStatusCommand?.setCameraPowerNotify(null)
            assertTrue(false, "exception Not supported characteristic")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                e.message!!.indexOf("Not supported", 0, true) >= 0,
                "exception Not supported characteristic",
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception Not supported characteristic. ${e.message}")
        }
    }

    /**
     * Notify empty for setCameraPowerNotify call.
     */
    @Test
    fun emptyDataTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "CAMERA_POWER") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setCameraPowerNotify { value, error ->
            assertTrue(error?.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
            assertNull(value)
            deferred.complete(Unit)
        }
        observer(ByteArray(0))
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "exception empty")
    }

    /**
     * Notify unknown for setCameraPowerNotify call.
     */
    @Test
    fun unknownDataTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "CAMERA_POWER") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setCameraPowerNotify { value, error ->
            assertTrue(error?.message!!.indexOf("Unknown value", 0, true) >= 0, "exception empty")
            assertNull(value)
            deferred.complete(Unit)
        }
        observer(255.toByte().toBytes())
        withTimeout(100000) {
            deferred.await()
        }
        assertTrue(true, "exception unknown")
    }
}
