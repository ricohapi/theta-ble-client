package com.ricoh360.thetableclient.wlancontrolv2

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.WlanControlCommandV2
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScanSsidStartTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    private fun initService(): WlanControlCommandV2 {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.wlanControlCommandV2
        assertNotNull(service)
        return service
    }

    @Test
    fun scanSsidStart() = runBlocking {
        val deferredStart = CompletableDeferred<Unit>()
        val deferredCompleted = CompletableDeferred<Unit>()
        var writeIndex = 0
        MockBlePeripheral.onWrite = { characteristic, data ->
            assertEquals(characteristic, BleCharacteristic.WRITE_SET_NETWORK_TYPE)
            when (writeIndex) {
                0 -> assertEquals(
                    data.decodeToString(),
                    "{\"type\":\"CL\"}".encodeToByteArray().decodeToString()
                )

                1 -> {
                    assertEquals(
                        data.decodeToString(),
                        "{\"type\":\"SCAN\"}".encodeToByteArray().decodeToString()
                    )
                    deferredStart.complete(Unit)
                }

                2 -> assertEquals(
                    data.decodeToString(),
                    "{\"type\":\"CL\"}".encodeToByteArray().decodeToString()
                )

                else -> assertTrue(false)
            }
            writeIndex += 1
        }

        val service = initService()

        val deferredObserve = CompletableDeferred<Unit>()
        var observer: ((ByteArray) -> Unit)? = null
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            assertEquals(
                characteristic,
                BleCharacteristic.NOTIFICATION_SCANNED_SSID
            )
            observer = collect
            deferredObserve.complete(Unit)
        }

        assertNotNull(service.thetaDevice.observeManager?.notifyList)
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_SSID))

        val deferredNotify = CompletableDeferred<Unit>()
        service.scanSsidStart(100, object : WlanControlCommandV2.ScanCallback {
            override fun onNotify(ssid: String) {
                assertTrue(ssid.startsWith("ssid_"))
                if (!deferredNotify.isCompleted) {
                    deferredNotify.complete(Unit)
                }
            }

            override fun onCompleted(ssidList: List<String>) {
                assertEquals(ssidList.size, 2)
                deferredCompleted.complete(Unit)
            }
        })
        withTimeout(10000) {
            deferredObserve.await()
        }
        assertNotNull(observer)
        assertEquals(service.ssidList.size, 0)
        assertTrue(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_SSID))
        withTimeout(10000) {
            deferredStart.await()
        }

        observer?.invoke("{\"ssid\":\"ssid_1\"}".encodeToByteArray())
        observer?.invoke("{\"ssid\":\"ssid_2\"}".encodeToByteArray())
        withTimeout(10000) {
            deferredNotify.await()
        }
        assertNotNull(service.scanTimeoutJob)
        withTimeout(10000) {
            deferredCompleted.await()
        }
        assertNull(service.scanTimeoutJob)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_SSID))
        assertEquals(service.ssidList.size, 2)
        assertEquals(service.ssidList[0], "ssid_1")
        assertEquals(service.ssidList[1], "ssid_2")
    }

    @Test
    fun updateSsidListTest() = runTest {
        val service = initService()

        service.addSsid("ssid_1")
        assertEquals(service.ssidList.size, 1)
        service.addSsid("ssid_1")
        assertEquals(service.ssidList.size, 1)
        assertEquals(service.ssidList[0], "ssid_1")
    }


    @Test
    fun stopScanTest() = runBlocking {
        val deferredCompleted = CompletableDeferred<Unit>()

        val service = initService()
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)

        service.scanSsidStart(10000, object : WlanControlCommandV2.ScanCallback {
            override fun onCompleted(ssidList: List<String>) {
                deferredCompleted.complete(Unit)
            }
        })
        assertNotNull(service.scanTimeoutJob)
        service.scanSsidStop()
        withTimeout(1000) {
            // Finish faster than scan timeout.
            deferredCompleted.await()
        }
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_SSID))
        assertNull(service.scanTimeoutJob)
    }

    @Test
    fun notConnectionTestForStart() = runTest {
        val service = initService()
        service.thetaDevice.disconnect()
        try {
            service.scanSsidStart(100, object : WlanControlCommandV2.ScanCallback {})
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

    @Test
    fun notConnectionTestForStop() = runTest {
        val service = initService()
        service.thetaDevice.disconnect()
        try {
            service.scanSsidStop()
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

    @Test
    fun writeExceptionTest() = runTest {
        val service = initService()

        MockBlePeripheral.onWrite = { characteristic, _ ->
            assertEquals(characteristic, BleCharacteristic.WRITE_SET_NETWORK_TYPE)
            throw Exception("write")
        }

        try {
            service.scanSsidStart(100, object : WlanControlCommandV2.ScanCallback {})
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
        assertNull(service.scanTimeoutJob)
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_SSID))
    }

    @Test
    fun notSupportedTest() = runBlocking {
        val service = initService()
        MockBlePeripheral.onContain = { characteristic ->
            characteristic != BleCharacteristic.NOTIFICATION_SCANNED_SSID
        }

        try {
            service.scanSsidStart(100, object : WlanControlCommandV2.ScanCallback {})
            assertTrue(false, "exception Not supported characteristic")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                e.message!!.indexOf("Not supported", 0, true) >= 0,
                "exception Not supported characteristic",
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception Not supported characteristic. ${e.message}")
        }
        assertNull(service.scanTimeoutJob)
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_SSID))
    }
}
