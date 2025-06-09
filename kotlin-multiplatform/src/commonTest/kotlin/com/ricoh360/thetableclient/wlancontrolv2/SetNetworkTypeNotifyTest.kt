package com.ricoh360.thetableclient.wlancontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.NetworkType
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

class SetNetworkTypeNotifyTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call setNetworkTypeNotify.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val deferredObserve = CompletableDeferred<Unit>()
        lateinit var observer: (ByteArray) -> Unit
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            if (characteristic == BleCharacteristic.WRITE_SET_NETWORK_TYPE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        val testNetworkType = NetworkType.DIRECT

        service.setNetworkTypeNotify { networkType, error ->
            assertNull(error)
            assertEquals(networkType, testNetworkType)
            deferred.complete(Unit)
        }
        withTimeout(1000) {
            deferredObserve.await()
        }

        observer(
            "{\"type\":\"${testNetworkType.value}\"}".encodeToByteArray()
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
            if (characteristic == BleCharacteristic.WRITE_SET_NETWORK_TYPE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        var deferred = CompletableDeferred<Unit>()
        val testNetworkType = NetworkType.DIRECT

        service.setNetworkTypeNotify { value, error ->
            assertNotNull(value)
            assertNull(error)
            deferred.complete(Unit)
        }

        withTimeout(1000) {
            deferredObserve.await()
        }

        val data = "{\"type\":\"${testNetworkType.value}\"}".encodeToByteArray()
        observer(data)
        withTimeout(1000) {
            deferred.await()
        }

        deferred = CompletableDeferred()
        service.setNetworkTypeNotify(null)

        val notifyList = device.observeManager?.notifyList
        assertNotNull(notifyList)
        assertNull(notifyList[BleCharacteristic.WRITE_SET_NETWORK_TYPE])

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
            service.setNetworkTypeNotify(null)
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
            characteristic != BleCharacteristic.WRITE_SET_NETWORK_TYPE
        }
        device.connect()
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        try {
            service.setNetworkTypeNotify { _, _ -> }
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
            if (characteristic == BleCharacteristic.WRITE_SET_NETWORK_TYPE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setNetworkTypeNotify { value, error ->
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
            if (characteristic == BleCharacteristic.WRITE_SET_NETWORK_TYPE) {
                observer = collect
                deferredObserve.complete(Unit)
            }
        }
        device.connect()

        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val deferred = CompletableDeferred<Unit>()

        service.setNetworkTypeNotify { thetaState, error ->
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
