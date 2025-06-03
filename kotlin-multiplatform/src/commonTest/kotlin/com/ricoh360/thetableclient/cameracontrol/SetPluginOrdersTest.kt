package com.ricoh360.thetableclient.cameracontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ble.PluginOrders
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetPluginOrdersTest {
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
     * call setPluginOrders.
     */
    @Test
    fun normalTest() = runBlocking {
        val valueList = listOf(
            PluginOrders(1, 2, 3),
            PluginOrders(2, 3, 1),
        )
        valueList.forEach {
            setValueTest(it)
        }
    }

    private fun setValueTest(testValue: PluginOrders) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "PLUGIN_ORDERS") {
                assertEquals(data[0], testValue.first.toByte(), "set value")
                assertEquals(data[1], testValue.second.toByte(), "set value")
                assertEquals(data[2], testValue.third.toByte(), "set value")
                deferred.complete(Unit)
            }
        }
        device.cameraControlCommands?.setPluginOrders(testValue)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setPluginOrders")
    }

    /**
     * Not connected exception for setPluginOrders call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val cameraControlCommands = device.cameraControlCommands
            device.disconnect()
            cameraControlCommands?.setPluginOrders(PluginOrders(1, 2, 3))
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

    /**
     * Read exception for setPluginOrders call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val testValue = PluginOrders(2, 1, 3)
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "PLUGIN_ORDERS") {
                assertEquals(data[0], testValue.first.toByte(), "set value")
                assertEquals(data[1], testValue.second.toByte(), "set value")
                assertEquals(data[2], testValue.third.toByte(), "set value")
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.cameraControlCommands?.setPluginOrders(testValue)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
