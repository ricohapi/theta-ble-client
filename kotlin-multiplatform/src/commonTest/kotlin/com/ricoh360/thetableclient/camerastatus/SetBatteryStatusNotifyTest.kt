package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.service.data.values.ChargingState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class SetBatteryStatusNotifyTest {
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
     * call setBatteryStatusNotify.
     */
    @Test
    fun normalTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()

        val testValue = ChargingState.CHARGED
        assertNotNull(testValue.ble)

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_STATUS") {
                observer = collect
            }
        }

        device.connect()
        delay(100)

        device.cameraStatusCommand?.setBatteryStatusNotify { value, error ->
            assertEquals(value, testValue)
            assertNull(error)
            deferred.complete(Unit)
        }
        val data = testValue.ble?.toBytes() ?: 255.toByte().toBytes()
        observer(data)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setBatteryStatusNotify")
    }

    /**
     * Set empty callback to setBatteryStatusNotify.
     */
    @Test
    fun setEmptyTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        var deferred = CompletableDeferred<Unit>()

        val testValue = ChargingState.CHARGING

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_STATUS") {
                observer = collect
            }
        }

        device.connect()
        delay(100)

        device.cameraStatusCommand?.setBatteryStatusNotify { value, error ->
            assertEquals(value, testValue)
            assertNull(error)
            deferred.complete(Unit)
        }
        val data = testValue.ble?.toBytes()?: 255.toByte().toBytes()
        observer(data)
        withTimeout(100) {
            deferred.await()
        }

        deferred = CompletableDeferred()
        device.cameraStatusCommand?.setBatteryStatusNotify(null)

        assertNull(device.observeManager!!.notifyList[BleCharacteristic.BATTERY_STATUS])

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
     * Not connected exception for setBatteryStatusNotify call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraStatusCommand = device.cameraStatusCommand
            device.disconnect()
            cameraStatusCommand?.setBatteryStatusNotify(null)
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
     * Not supported characteristic exception for setBatteryStatusNotify call.
     */
    @Test
    fun notSupportedTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        MockBlePeripheral.onContain = { characteristic ->
            characteristic.name != "BATTERY_STATUS"
        }

        device.connect()

        try {
            device.cameraStatusCommand?.setBatteryStatusNotify(null)
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
     * Notify empty for setBatteryStatusNotify call.
     */
    @Test
    fun emptyDataTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_STATUS") {
                observer = collect
            }
        }

        device.connect()
        delay(100)

        device.cameraStatusCommand?.setBatteryStatusNotify { value, error ->
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
     * Notify unknown for setBatteryStatusNotify call.
     */
    @Test
    fun unknownDataTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val deferred = CompletableDeferred<Unit>()

        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic.name == "BATTERY_STATUS") {
                observer = collect
            }
        }

        device.connect()
        delay(100)

        device.cameraStatusCommand?.setBatteryStatusNotify { value, error ->
            assertTrue(error?.message!!.indexOf("Unknown value", 0, true) >= 0, "exception empty")
            assertNull(value)
            deferred.complete(Unit)
        }
        observer(255.toByte().toBytes())
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "exception unknown")
    }
}
