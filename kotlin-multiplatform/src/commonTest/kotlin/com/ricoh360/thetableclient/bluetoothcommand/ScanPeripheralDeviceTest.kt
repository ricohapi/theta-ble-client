package com.ricoh360.thetableclient.bluetoothcommand

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.BluetoothControlCommand
import com.ricoh360.thetableclient.service.data.values.PeripheralDeviceStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ScanPeripheralDeviceTest {
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
    fun scanPeripheralDevice() = runBlocking {
        val service = initService()
        val deferredStart = CompletableDeferred<Unit>()
        val deferredCompleted = CompletableDeferred<Unit>()

        var observer: ((ByteArray) -> Unit)? = null
        MockBlePeripheral.onObserve = { characteristic, collect: (ByteArray) -> Unit ->
            assertEquals(
                characteristic,
                BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
            )
            observer = collect
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            assertEquals(characteristic, BleCharacteristic.SCAN_BLUETOOTH_PERIPHERAL_DEVICE)
            assertEquals(
                data.decodeToString(),
                "{\"type\":\"SCAN\"}".encodeToByteArray().decodeToString()
            )
            deferredStart.complete(Unit)
        }

        launch {
            val peripheralDeviceList = service.scanPeripheralDevice(100)
            assertNotNull(peripheralDeviceList)
            assertEquals(peripheralDeviceList.size, 2)
            val peripheralDevice = peripheralDeviceList[0]
            assertEquals(peripheralDevice.device, "Shutter1")
            assertEquals(peripheralDevice.status, PeripheralDeviceStatus.IDLE)
            assertEquals(peripheralDevice.pairing, false)
            assertEquals(peripheralDevice.macAddress, "2A:07:98:00:28:11")
            deferredCompleted.complete(Unit)
        }
        withTimeout(10000) {
            deferredStart.await()
        }
        assertNotNull(observer)

        observer?.invoke("{\"status\":\"idle\",\"device\":\"Shutter1\",\"pairing\":false,\"macAddress\":\"2A:07:98:00:28:11\"}".encodeToByteArray())
        observer?.invoke("{\"status\":\"idle\",\"device\":\"Shutter2\",\"pairing\":false,\"macAddress\":\"2A:07:98:00:28:12\"}".encodeToByteArray())

        withTimeout(10000) {
            deferredCompleted.await()
        }
    }

    @Test
    fun notConnectionTest() = runTest {
        val service = initService()
        service.thetaDevice.disconnect()
        try {
            service.scanPeripheralDevice(100)
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
            service.scanPeripheralDevice(100)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }

    @Test
    fun notSupportedTest() = runBlocking {
        val service = initService()
        MockBlePeripheral.onContain = { characteristic ->
            characteristic != BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
        }

        try {
            service.scanPeripheralDevice(100)
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
}
