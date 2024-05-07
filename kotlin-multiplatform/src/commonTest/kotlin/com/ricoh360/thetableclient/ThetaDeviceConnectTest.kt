package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.MocBleAdvertisement
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ThetaDeviceConnectTest {

    private val testUuid = "6BEDD7A3-4E01-4FE4-9DFB-03BFF23ECFD3"
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
     * call connect.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect(testUuid)
        assertEquals(device.name, devName, "connect")
        assertEquals(device.uuid, testUuid, "connect")

        assertEquals(device.observeManager!!.notifyList.size, device.notifyCharacteristicList.size)
    }

    /**
     * Exception for newPeripheral call.
     */
    @Test
    fun newPeripheralExceptionTest() = runTest {
        MocBleAdvertisement.onNewPeripheral = {
            throw Exception("peripheral")
        }
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        try {
            device.connect()
            assertTrue(false, "exception newPeripheral")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("peripheral", 0, true) >= 0, "exception newPeripheral")
        } catch (e: Throwable) {
            assertTrue(false, "exception newPeripheral. ${e.message}")
        }
    }

    /**
     * Timeout for newPeripheral call.
     */
    @Test
    fun newPeripheralTimeoutTest() = runBlocking {
        val timeoutPeripheral = TIMEOUT_PERIPHERAL
        MocBleAdvertisement.onNewPeripheral = {
            runBlocking {
                delay(timeoutPeripheral.toLong() + 100)
            }
            null
        }
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        try {
            device.connect()
            assertTrue(false, "exception newPeripheral")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("timeout", 0, true) >= 0, "exception newPeripheral")
        } catch (e: Throwable) {
            assertTrue(false, "exception newPeripheral. ${e.message}")
        }
    }

    /**
     * Exception for BlePeripheral.connect call.
     */
    @Test
    fun connectExceptionTest() = runBlocking {
        MockBlePeripheral.onConnect = {
            throw Exception("connect")
        }
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        try {
            device.connect()
            assertTrue(false, "exception connect")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("connect", 0, true) >= 0, "exception connect")
        } catch (e: Throwable) {
            assertTrue(false, "exception connect. ${e.message}")
        }
    }

    /**
     * Timeout for BlePeripheral.connect call.
     */
    @Test
    fun connectTimeoutTest() = runBlocking {
        val timeoutConnect = 1000
        val timeout = ThetaBle.Timeout(
            timeoutConnect = timeoutConnect
        )
        MockBlePeripheral.onConnect = {
            delay(timeoutConnect.toLong() + 1000)
        }
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName), timeout)
        try {
            device.connect()
            assertTrue(false, "exception connect timeout")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("timeout", 0, true) >= 0, "exception connect timeout")
        } catch (e: Throwable) {
            assertTrue(false, "exception connect timeout. ${e.message}")
        }
    }

    /**
     * Exception for requestMtu call.
     */
    @Test
    fun requestMtuExceptionTest() = runBlocking {
        MockBlePeripheral.onRequestMtu = {
            throw Exception("requestMtu")
        }
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        try {
            device.connect()
            assertTrue(false, "exception requestMtu")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("requestMtu", 0, true) >= 0, "exception requestMtu")
        } catch (e: Throwable) {
            assertTrue(false, "exception requestMtu. ${e.message}")
        }
    }

    /**
     * Exception for authBluetoothDevice call.
     */
    @Test
    fun authExceptionTest() = runBlocking {
        MockBlePeripheral.onWrite = { _, _ ->
            throw Exception("authBluetoothDevice")
        }
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        try {
            device.connect(testUuid)
            assertTrue(false, "exception authBluetoothDevice")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(
                e.message!!.indexOf("authBluetoothDevice", 0, true) >= 0,
                "exception authBluetoothDevice"
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception authBluetoothDevice. ${e.message}")
        }
    }

    /**
     * Peripheral null Exception for authBluetoothDevice call.
     */
    @Test
    fun authNullPeripheralExceptionTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        MockBlePeripheral.onRequestMtu = {
            device.peripheral = null
        }
        try {
            device.connect(testUuid)
            assertTrue(false, "exception authBluetoothDevice")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                e.message!!.indexOf("Not connected", 0, true) >= 0,
                "exception authBluetoothDevice"
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception authBluetoothDevice. ${e.message}")
        }
    }
}
