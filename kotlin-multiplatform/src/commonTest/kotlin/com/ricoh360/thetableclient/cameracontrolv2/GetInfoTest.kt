package com.ricoh360.thetableclient.cameracontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.ThetaModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetInfoTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * getInfo for THETA X.
     */
    @Test
    fun getInfoThetaXTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getInfo/info_x.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_INFO)
            jsonText.encodeToByteArray()
        }

        val thetaInfo = service.getInfo()
        assertEquals(thetaInfo.manufacturer, "Ricoh Company, Ltd.")
        assertEquals(thetaInfo.model, ThetaModel.THETA_X)
        assertEquals(thetaInfo.serialNumber, "12345678")
        assertEquals(thetaInfo.wlanMacAddress, "AA:AA:AA:AA:AA:AA")
        assertEquals(thetaInfo.bluetoothMacAddress, "99:99:99:99:99:99")
        assertEquals(thetaInfo.firmwareVersion, "2.21.0")
        assertEquals(thetaInfo.uptime, 1213)
    }

    /**
     * getInfo for Unknown camera.
     */
    @Test
    fun getInfoUnknownTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getInfo/info_unknown.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_INFO)
            jsonText.encodeToByteArray()
        }

        val thetaInfo = service.getInfo()
        assertEquals(thetaInfo.manufacturer, "Ricoh Company, Ltd.")
        assertEquals(thetaInfo.model, ThetaModel.UNKNOWN)
        assertEquals(thetaInfo.serialNumber, "12345678")
        assertNull(thetaInfo.wlanMacAddress)
        assertNull(thetaInfo.bluetoothMacAddress)
        assertEquals(thetaInfo.firmwareVersion, "2.21.0")
        assertEquals(thetaInfo.uptime, 1213)
    }

    /**
     * Serialization exception.
     */
    @Test
    fun notJsonTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/err_test.txt").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_INFO)
            jsonText.encodeToByteArray()
        }

        try {
            service.getInfo()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleSerializationException) {
            assertTrue(true, "exception")
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }

    /**
     * Not connected exception.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        // setup not connected
        device.peripheral = null

        try {
            service.getInfo()
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
     * Read exception.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            service.getInfo()
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("read", 0, true) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read empty exception.
     */
    @Test
    fun emptyBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            service.getInfo()
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
