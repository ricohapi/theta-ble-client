package com.ricoh360.thetableclient.wlancontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.transferred.CameraConnectedInfo
import com.ricoh360.thetableclient.transferred.CameraConnectedWifiInfo
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetConnectedWifiInfoTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call getConnectedWifiInfo.
     */
    @Test
    fun getConnectedWifiInfoTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val testValue = CameraConnectedWifiInfo(
            wifiInfo = CameraConnectedInfo(ssid = "ssid_test", isConnected = true, isInternetAccessible = true)
        )
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.READ_CONNECTED_WIFI_INFO)
            Json.encodeToString(testValue).encodeToByteArray()
        }

        val connectedWifiInfo = service.getConnectedWifiInfo()
        assertNotNull(connectedWifiInfo.wifiInfo)
        assertEquals(connectedWifiInfo.wifiInfo?.ssid, "ssid_test")
        assertEquals(connectedWifiInfo.wifiInfo?.isConnected, true)
        assertEquals(connectedWifiInfo.wifiInfo?.isInternetAccessible, true)
    }

    /**
     * Serialization exception.
     */
    @Test
    fun notJsonTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/err_test.txt").readText()

        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.READ_CONNECTED_WIFI_INFO)
            jsonText.encodeToByteArray()
        }

        try {
            service.getConnectedWifiInfo()
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
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        // setup not connected
        device.peripheral = null

        try {
            service.getConnectedWifiInfo()
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
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            service.getConnectedWifiInfo()
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
        val service = device.wlanControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            service.getConnectedWifiInfo()
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
