package com.ricoh360.thetableclient.bluetoothcommand

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DeletePeripheralDeviceTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    @Test
    fun deletePeripheralDeviceTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }

        var deferredWrite = CompletableDeferred<Unit>()
        MockBlePeripheral.onWrite = { characteristic, data ->
            assertEquals(characteristic, BleCharacteristic.DELETE_BLUETOOTH_PERIPHERAL_DEVICE)
            assertEquals(
                data.decodeToString(),
                "{\"macAddress\":\"address\"}".encodeToByteArray().decodeToString()
            )
            deferredWrite.complete(Unit)
        }

        val service = device.bluetoothControlCommand
        assertNotNull(service)
        service.deletePeripheralDevice("address")
        withTimeout(100) {
            deferredWrite.await()
        }
        assertTrue(true, "deletePeripheralDevice 1")

        deferredWrite = CompletableDeferred()
        service.deletePeripheralDevice(
            PeripheralDevice(
                "name1",
                "address",
                true,
                PeripheralDeviceStatus.IDLE
            )
        )
        withTimeout(100) {
            deferredWrite.await()
        }
        assertTrue(true, "deletePeripheralDevice 2")
    }

    @Test
    fun exceptionApiTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.bluetoothControlCommand
        assertNotNull(service)

        // setup not connected
        device.peripheral = null

        try {
            service.deletePeripheralDevice("address")
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
    fun exceptionBleTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.bluetoothControlCommand
        assertNotNull(service)

        MockBlePeripheral.onWrite = { _, _ ->
            throw Exception("write")
        }

        try {
            service.deletePeripheralDevice("address")
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
