package com.ricoh360.thetableclient.wlancontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.transferred.CameraConnectedInfo
import com.ricoh360.thetableclient.transferred.CameraConnectedWifiInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetConnectedWifiInfoNotifyTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call SetConnectedWifiInfoNotifyTest.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val deferredObserve = CompletableDeferred<Unit>()
        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic == BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setConnectedWifiInfoNotify { value, error ->
            assertNull(error)
            assertNotNull(value?.wifiInfo)
            assertEquals(value?.wifiInfo?.ssid, "ssid_test")
            assertEquals(value?.wifiInfo?.isConnected, true)
            assertEquals(value?.wifiInfo?.isInternetAccessible, true)
            deferred.complete(Unit)
        }
        withTimeout(1000) {
            deferredObserve.await()
        }

        val testValue = CameraConnectedWifiInfo(
            wifiInfo = CameraConnectedInfo(
                ssid = "ssid_test",
                isConnected = true,
                isInternetAccessible = true
            )
        )
        observer(
            Json.encodeToString(testValue).encodeToByteArray()
        )

        withTimeout(1000) {
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
            if (characteristic == BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        var deferred = CompletableDeferred<Unit>()

        service.setConnectedWifiInfoNotify { value, error ->
            assertNotNull(value)
            assertNull(error)
            deferred.complete(Unit)
        }

        withTimeout(1000) {
            deferredObserve.await()
        }

        val testValue = CameraConnectedWifiInfo(
            wifiInfo = CameraConnectedInfo(
                ssid = "ssid_test",
                isConnected = true,
                isInternetAccessible = true
            )
        )
        val data = Json.encodeToString(testValue).encodeToByteArray()

        observer(data)
        withTimeout(1000) {
            deferred.await()
        }

        deferred = CompletableDeferred()
        service.setConnectedWifiInfoNotify(null)

        val notifyList = device.observeManager?.notifyList
        assertNotNull(notifyList)
        assertNull(notifyList[BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO])

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
     * Not connected exception.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        // setup not connected
        device.peripheral = null

        try {
            service.setConnectedWifiInfoNotify(null)
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
            characteristic != BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO
        }
        device.connect()
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        try {
            service.setConnectedWifiInfoNotify { _, _ -> }
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
            if (characteristic == BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setConnectedWifiInfoNotify { value, error ->
            assertTrue(error?.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
            assertNull(value)
            deferred.complete(Unit)
        }

        withTimeout(1000) {
            deferredObserve.await()
        }
        observer(ByteArray(0))
        withTimeout(100) {
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
            if (characteristic == BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setConnectedWifiInfoNotify { thetaState, error ->
            assertNull(thetaState)
            assertNotNull(error)
            assertTrue(error is ThetaBle.ThetaBleSerializationException)
            deferred.complete(Unit)
        }

        withTimeout(1000) {
            deferredObserve.await()
        }

        val jsonText = Resource("src/commonTest/resources/err_test.txt").readText()
        val data = jsonText.encodeToByteArray()
        observer(data)

        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "notify")
    }
}
