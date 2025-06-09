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

class SetStateNotifyTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
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
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val deferredObserve = CompletableDeferred<Unit>()
        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic == BleCharacteristic.NOTIFY_STATE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setStateNotify { thetaState, error ->
            assertNull(error)
            assertNotNull(thetaState)
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
            deferred.complete(Unit)
        }

        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getState/state.json").readText()
        val data = jsonText.encodeToByteArray()
        observer(data)

        withTimeout(500) {
            deferred.await()
        }
        assertTrue(true, "notify")
    }

    /**
     * Set empty callback.
     */
    @Test
    fun setEmptyTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val deferredObserve = CompletableDeferred<Unit>()
        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic == BleCharacteristic.NOTIFY_STATE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        val service = device.cameraControlCommandV2
        assertNotNull(service)

        var deferred = CompletableDeferred<Unit>()

        service.setStateNotify { value, error ->
            assertNotNull(value)
            assertNull(error)
            deferred.complete(Unit)
        }
        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getState/state.json").readText()
        val data = jsonText.encodeToByteArray()
        observer(data)
        withTimeout(1000) {
            deferred.await()
        }

        deferred = CompletableDeferred()
        service.setStateNotify(null)

        val notifyList = device.observeManager?.notifyList
        assertNotNull(notifyList)
        assertNull(notifyList[BleCharacteristic.NOTIFY_STATE])

        observer(data)
        try {
            withTimeout(500) {
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
     * Not connected exception.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        // setup not connected
        device.peripheral = null

        try {
            service.setStateNotify(null)
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
     * Not supported characteristic exception.
     */
    @Test
    fun notSupportedTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        MockBlePeripheral.onContain = { characteristic ->
            characteristic != BleCharacteristic.NOTIFY_STATE
        }
        device.connect()
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        try {
            service.setStateNotify(null)
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
     * Notify empty response
     */
    @Test
    fun emptyDataTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val deferredObserve = CompletableDeferred<Unit>()
        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic == BleCharacteristic.NOTIFY_STATE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setStateNotify { value, error ->
            assertTrue(error?.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
            assertNull(value)
            deferred.complete(Unit)
        }

        observer(ByteArray(0))
        withTimeout(500) {
            deferred.await()
        }
        assertTrue(true, "exception empty")
    }

    /**
     * Serialization exception.
     */
    @Test
    fun notJsonTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val deferredObserve = CompletableDeferred<Unit>()
        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic == BleCharacteristic.NOTIFY_STATE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        withTimeout(1000) {
            deferredObserve.await()
        }

        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setStateNotify { thetaState, error ->
            assertNull(thetaState)
            assertNotNull(error)
            assertTrue(error is ThetaBle.ThetaBleSerializationException)
            deferred.complete(Unit)
        }

        val jsonText = Resource("src/commonTest/resources/err_test.txt").readText()
        val data = jsonText.encodeToByteArray()
        observer(data)

        withTimeout(500) {
            deferred.await()
        }
        assertTrue(true, "notify")
    }
}
