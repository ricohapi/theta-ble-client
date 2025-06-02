package com.ricoh360.thetableclient.bluetoothcommand

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.BluetoothControlCommand
import com.ricoh360.thetableclient.service.data.PeripheralDevice
import com.ricoh360.thetableclient.service.data.values.PeripheralDeviceStatus
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

class ScanPeripheralDeviceStartTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    private fun initService(): BluetoothControlCommand {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.bluetoothControlCommand
        assertNotNull(service)
        return service
    }

    @Test
    fun scanPeripheralDeviceStart() = runBlocking {
        val deferredStart = CompletableDeferred<Unit>()
        val deferredCompleted = CompletableDeferred<Unit>()
        MockBlePeripheral.onWrite = { characteristic, data ->
            assertEquals(characteristic, BleCharacteristic.SCAN_BLUETOOTH_PERIPHERAL_DEVICE)
            assertEquals(
                data.decodeToString(),
                "{\"type\":\"SCAN\"}".encodeToByteArray().decodeToString()
            )
            deferredStart.complete(Unit)
        }

        val service = initService()

        val deferredObserve = CompletableDeferred<Unit>()
        var observer: ((ByteArray) -> Unit)? = null
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            assertEquals(
                characteristic,
                BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
            )
            observer = collect
            deferredObserve.complete(Unit)
        }

        assertNotNull(service.thetaDevice.observeManager?.notifyList)
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE))

        val deferredNotify = CompletableDeferred<Unit>()
        service.scanPeripheralDeviceStart(100, object : BluetoothControlCommand.ScanCallback {
            override fun onNotify(peripheralDevice: PeripheralDevice) {
                assertTrue(peripheralDevice.device.startsWith("Shutter"))
                assertEquals(peripheralDevice.status, PeripheralDeviceStatus.IDLE)
                assertEquals(peripheralDevice.pairing, false)
                assertTrue(peripheralDevice.macAddress.startsWith("2A:07:98:00:28:1"))
                if (!deferredNotify.isCompleted) {
                    deferredNotify.complete(Unit)
                }
            }

            override fun onCompleted(peripheralDeviceList: List<PeripheralDevice>) {
                assertEquals(peripheralDeviceList.size, 2)
                deferredCompleted.complete(Unit)
            }
        })
        assertTrue(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE))
        assertNotNull(observer)
        assertEquals(service.peripheralDeviceList.size, 0)

        withTimeout(10000) {
            deferredStart.await()
        }
        observer?.invoke("{\"status\":\"idle\",\"device\":\"Shutter1\",\"pairing\":false,\"macAddress\":\"2A:07:98:00:28:11\"}".encodeToByteArray())
        observer?.invoke("{\"status\":\"idle\",\"device\":\"Shutter2\",\"pairing\":false,\"macAddress\":\"2A:07:98:00:28:12\"}".encodeToByteArray())
        withTimeout(10000) {
            deferredNotify.await()
        }
        assertNotNull(service.scanTimeoutJob)
        withTimeout(10000) {
            deferredCompleted.await()
        }
        assertNull(service.scanTimeoutJob)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE))
        assertEquals(service.peripheralDeviceList.size, 2)
        val peripheralDevice = service.peripheralDeviceList[0]
        assertEquals(peripheralDevice.device, "Shutter1")
        assertEquals(peripheralDevice.status, PeripheralDeviceStatus.IDLE)
        assertEquals(peripheralDevice.pairing, false)
        assertEquals(peripheralDevice.macAddress, "2A:07:98:00:28:11")
    }

    @Test
    fun updatePeripheralDeviceListTest() = runTest {
        val service = initService()

        service.addPeripheralDevice(
            PeripheralDevice(
                "name1",
                "address",
                true,
                PeripheralDeviceStatus.IDLE
            )
        )
        assertEquals(service.peripheralDeviceList.size, 1)
        service.addPeripheralDevice(
            PeripheralDevice(
                "name2",
                "address",
                false,
                PeripheralDeviceStatus.CONNECTED
            )
        )
        assertEquals(service.peripheralDeviceList.size, 1)
        assertEquals(service.peripheralDeviceList[0].macAddress, "address")
        assertEquals(service.peripheralDeviceList[0].device, "name2")
        assertEquals(service.peripheralDeviceList[0].pairing, false)
        assertEquals(service.peripheralDeviceList[0].status, PeripheralDeviceStatus.CONNECTED)
    }

    @Test
    fun scanPeripheralDeviceUpdate() = runBlocking {
        val deferredStart = CompletableDeferred<Unit>()
        val deferredCompleted = CompletableDeferred<Unit>()

        var observer: ((ByteArray) -> Unit)? = null
        MockBlePeripheral.onObserve = { _, collect: (ByteArray) -> Unit ->
            observer = collect
            deferredStart.complete(Unit)
        }

        val service = initService()
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)

        var notifyCount = 0
        service.scanPeripheralDeviceStart(100, object : BluetoothControlCommand.ScanCallback {
            override fun onNotify(peripheralDevice: PeripheralDevice) {
                notifyCount += 1
                println("onNotify ${peripheralDevice.device} ${peripheralDevice.status}")
            }

            override fun onCompleted(peripheralDeviceList: List<PeripheralDevice>) {
                deferredCompleted.complete(Unit)
            }
        })
        withTimeout(100) {
            deferredStart.await()
        }
        observer?.invoke("{\"status\":\"idle\",\"device\":\"Shutter\",\"pairing\":false,\"macAddress\":\"2A:07:98:00:28:11\"}".encodeToByteArray())
        observer?.invoke("{\"status\":\"connected\",\"device\":\"Shutter\",\"pairing\":true,\"macAddress\":\"2A:07:98:00:28:11\"}".encodeToByteArray())
        observer?.invoke("{\"status\":\"idle\",\"device\":\"Shutter2\",\"pairing\":false,\"macAddress\":\"2A:07:98:00:28:12\"}".encodeToByteArray())

        withTimeout(10000) {
            deferredCompleted.await()
        }
        assertEquals(notifyCount, 3)
        assertEquals(service.peripheralDeviceList.size, 2)
        val peripheralDevice = service.peripheralDeviceList[0]
        assertEquals(peripheralDevice.device, "Shutter")
        assertEquals(peripheralDevice.status, PeripheralDeviceStatus.CONNECTED)
        assertEquals(peripheralDevice.pairing, true)
        assertEquals(peripheralDevice.macAddress, "2A:07:98:00:28:11")
        assertEquals(service.peripheralDeviceList[1].device, "Shutter2")
    }

    @Test
    fun stopScanTest() = runBlocking {
        val deferredCompleted = CompletableDeferred<Unit>()

        val service = initService()
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)

        service.scanPeripheralDeviceStart(10000, object : BluetoothControlCommand.ScanCallback {
            override fun onCompleted(peripheralDeviceList: List<PeripheralDevice>) {
                deferredCompleted.complete(Unit)
            }
        })
        assertNotNull(service.scanTimeoutJob)
        service.scanPeripheralDeviceStop()
        withTimeout(1000) {
            // Finish faster than scan timeout.
            deferredCompleted.await()
        }
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE))
        assertNull(service.scanTimeoutJob)
    }

    @Test
    fun notConnectionTestForStart() = runTest {
        val service = initService()
        service.thetaDevice.disconnect()
        try {
            service.scanPeripheralDeviceStart(100, object : BluetoothControlCommand.ScanCallback {})
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
            service.scanPeripheralDeviceStop()
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
            assertEquals(characteristic, BleCharacteristic.SCAN_BLUETOOTH_PERIPHERAL_DEVICE)
            throw Exception("write")
        }

        try {
            service.scanPeripheralDeviceStart(100, object : BluetoothControlCommand.ScanCallback {})
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
        assertNull(service.scanTimeoutJob)
        val notifyList = service.thetaDevice.observeManager?.notifyList
        assertNotNull(notifyList)
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE))
    }

    @Test
    fun notSupportedTest() = runBlocking {
        val service = initService()
        MockBlePeripheral.onContain = { characteristic ->
            characteristic != BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
        }

        try {
            service.scanPeripheralDeviceStart(100, object : BluetoothControlCommand.ScanCallback {})
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
        assertFalse(notifyList.containsKey(BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE))
    }
}
