package com.ricoh360.thetableclient.camerainfo

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetModelNumberTest {
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
     * call getModelNumber.
     */
    @Test
    fun normalTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val name = "THETA Z1"
        MockBlePeripheral.onRead = {
            assertEquals(it.name, "MODEL_NUMBER")
            name.encodeToByteArray()
        }
        val model = device.cameraInformation?.getModelNumber()
        assertEquals(model, name, "getModelNumber")
    }

    /**
     * Not connected exception for getModelNumber call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        try {
            val cameraInformation = device.cameraInformation
            device.disconnect()
            cameraInformation?.getModelNumber()
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
     * Read exception for getModelNumber call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            device.connect()
            device.cameraInformation?.getModelNumber()
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("read", 0, true) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read empty for getModelNumber call.
     */
    @Test
    fun emptyBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            device.connect()
            device.cameraInformation?.getModelNumber()
            assertTrue(false, "exception empty")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Empty data", 0, true) >= 0, "exception empty")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception empty")
        } catch (e: Throwable) {
            assertTrue(false, "exception empty. ${e.message}")
        }
    }
}
