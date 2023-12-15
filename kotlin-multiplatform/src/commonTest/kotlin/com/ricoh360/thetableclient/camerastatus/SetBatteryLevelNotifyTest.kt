package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.toBytes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetBatteryLevelNotifyTest {
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
     * call setBatteryLevelNotify.
     */
    @Test
    fun normalTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        val testValue = 99

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_LEVEL") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setBatteryLevelNotify { value, error ->
            assertEquals(value, testValue)
            assertNull(error)
            deferred.complete(Unit)
        }
        val data = testValue.toByte().toBytes()
        observer(data)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setBatteryLevelNotify")
    }

    /**
     * Set empty callback to setBatteryLevelNotify.
     */
    @Test
    fun setEmptyTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        var deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        val testValue = 99

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_LEVEL") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setBatteryLevelNotify { value, error ->
            assertEquals(value, testValue)
            assertNull(error)
            deferred.complete(Unit)
        }
        val data = testValue.toByte().toBytes()
        observer(data)
        withTimeout(100) {
            deferred.await()
        }

        deferred = CompletableDeferred()
        device.cameraStatusCommand?.setBatteryLevelNotify(null)

        assertNull(device.observeManager!!.notifyList[BleCharacteristic.BATTERY_LEVEL])

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
     * Not connected exception for setBatteryLevelNotify call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraStatusCommand = device.cameraStatusCommand
            device.disconnect()
            cameraStatusCommand?.setBatteryLevelNotify(null)
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
     * Not supported characteristic exception for setBatteryLevelNotify call.
     */
    @Test
    fun notSupportedTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        MockBlePeripheral.onContain = { characteristic ->
            characteristic.name != "BATTERY_LEVEL"
        }

        device.connect()

        try {
            device.cameraStatusCommand?.setBatteryLevelNotify(null)
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
     * Notify empty for setBatteryLevelNotify call.
     */
    @Test
    fun emptyDataTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()
        val deferredObserve = CompletableDeferred<Unit>()

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_LEVEL") {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }

        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        device.cameraStatusCommand?.setBatteryLevelNotify { value, error ->
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
}
