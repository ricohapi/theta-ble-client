package com.ricoh360.thetableclient.cameracontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ble.PluginList
import com.ricoh360.thetableclient.toBytes
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPluginListTest {
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
     * call getPluginList.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val list = listOf(3, 2, 1)
        val targetValue = PluginList(list)
        MockBlePeripheral.onRead = {
            assertEquals(it.name, "PLUGIN_LIST")
            targetValue.toBleData()
        }
        val result = device.cameraControlCommands?.getPluginList()
        assertEquals(list.size, result?.plugins?.size, "getPluginList")
        assertEquals(list[0], result?.plugins?.get(0) ?: -1, "getPluginList")
        assertEquals(list[1], result?.plugins?.get(1) ?: -1, "getPluginList")
        assertEquals(list[2], result?.plugins?.get(2) ?: -1, "getPluginList")
    }

    /**
     * Not connected exception for getPluginList call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraControlCommands = device.cameraControlCommands
            device.disconnect()
            cameraControlCommands?.getPluginList()
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
     * Read exception for getPluginList call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            device.connect()
            device.cameraControlCommands?.getPluginList()
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("read", 0, true) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read empty for getPluginList call.
     */
    @Test
    fun emptyBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            device.connect()
            device.cameraControlCommands?.getPluginList()
            assertTrue(false, "exception empty")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception empty")
        } catch (e: Throwable) {
            assertTrue(false, "exception empty. ${e.message}")
        }
    }

    /**
     * Read unknown for getPluginList call.
     */
    @Test
    fun unknownBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            2.toByte().toBytes()
        }

        try {
            device.connect()
            device.cameraControlCommands?.getPluginList()
            assertTrue(false, "exception unknown")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Unknown value", 0, true) >= 0, "exception unknown")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception unknown")
        } catch (e: Throwable) {
            assertTrue(false, "exception unknown. ${e.message}")
        }
    }
}
