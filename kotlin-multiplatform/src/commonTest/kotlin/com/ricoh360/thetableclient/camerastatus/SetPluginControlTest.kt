package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.service.data.ble.PluginControl
import com.ricoh360.thetableclient.service.data.values.PluginPowerStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class SetPluginControlTest {
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
     * call setPluginControl.
     */
    @Test
    fun normalTest() = runBlocking {
        val valueList = listOf(
            PluginControl(PluginPowerStatus.RUNNING, 1),
            PluginControl(PluginPowerStatus.STOP, 2),
        )
        valueList.forEach {
            setValueTest(it)
        }
    }

    private fun setValueTest(testValue: PluginControl) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "PLUGIN_CONTROL") {
                assertEquals(data[0], testValue.pluginControl.ble, "set value")
                assertEquals(data[1], testValue.plugin?.toByte(), "set value")
                deferred.complete(Unit)
            }
        }
        device.cameraStatusCommand?.setPluginControl(testValue)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setPluginControl")
    }

    /**
     * Not connected exception for setPluginControl call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraStatusCommand = device.cameraStatusCommand
            device.disconnect()
            cameraStatusCommand?.setPluginControl(PluginControl(PluginPowerStatus.STOP, null))
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
     * Read exception for setPluginControl call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val testValue = PluginControl(PluginPowerStatus.RUNNING, 1)
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "PLUGIN_CONTROL") {
                assertEquals(data[0], testValue.pluginControl.ble, "set value")
                assertEquals(data[1], testValue.plugin?.toByte(), "set value")
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.cameraStatusCommand?.setPluginControl(testValue)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
