package com.ricoh360.thetableclient.cameracontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetOptionsByStringTest {
    private val devName = "AA12345678"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * getOption.
     */
    @Test
    fun getOptionTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getOptions/options.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_OPTIONS)
            jsonText.encodeToByteArray()
        }

        val optionMap = service.getOptionsByString(
            listOf(
                "_cameraPower",
                "captureMode",
                "_networkType",
                "_password",
                "_ssid",
                "_username",
                "_wlanAntennaConfig",
                "_wlanFrequency",
                "_accessInfo",
            )
        )
        assertEquals(optionMap["_cameraPower"], "on")
        assertEquals(optionMap["captureMode"], "image")
        assertEquals(optionMap["_networkType"], "AP")
        assertEquals(optionMap["_password"], "password123")
        assertEquals(optionMap["_ssid"], "ssid123")
        assertEquals(optionMap["_username"], "adminUser")
        assertEquals(optionMap["_wlanAntennaConfig"], "MIMO")
        assertEquals(optionMap["_wlanFrequency"], 2.4)
        assertEquals(
            optionMap["_accessInfo"], mapOf(
                "ssid" to "test_ssid",
                "ipAddress" to "10.0.0.222",
                "subnetMask" to "255.255.255.0",
                "defaultGateway" to "10.0.0.2",
                "proxyURL" to "proxy-url",
                "frequency" to "2.4",
                "wlanSignalStrength" to -51,
                "wlanSignalLevel" to 4,
                "lteSignalStrength" to 0,
                "lteSignalLevel" to 0,
                "_dhcpLeaseAddress" to listOf<Map<String, Any>>(
                    mapOf(
                        "ipAddress" to "10.0.0.3",
                        "macAddress" to "58:38:79:9f:00:00",
                        "hostName" to "host_name"
                    )
                ),
            )
        )
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
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/err_test.txt").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_OPTIONS)
            jsonText.encodeToByteArray()
        }

        try {
            service.getOptionsByString(listOf("captureMode"))
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
            service.getOptionsByString(listOf("captureMode"))
            assertTrue(false, "exception Not connected")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                (e.message?.indexOf("Not connected", 0, true) ?: -1) >= 0,
                "exception Not connected"
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception Not connected. ${e.message}")
        }
    }

    /**
     * Write exception.
     */
    @Test
    fun writeExceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onWrite = { _, _ ->
            throw Exception("write")
        }

        try {
            service.getOptionsByString(listOf("captureMode"))
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue((e.message?.indexOf("write", 0, true) ?: -1) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read exception.
     */
    @Test
    fun readExceptionBleTest() = runBlocking {
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
            service.getOptionsByString(listOf("captureMode"))
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue((e.message?.indexOf("read", 0, true) ?: -1) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read exception.
     */
    @Test
    fun responseExceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            val data = ByteArray(1)
            data[0] = 0x80.toByte()
            data
        }

        try {
            service.getOptionsByString(listOf("captureMode"))
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            assertEquals(e.message, "Disabled Command", "response error")
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
            service.getOptionsByString(listOf("captureMode"))
            assertTrue(false, "exception empty")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue((e.message?.indexOf("Empty data", 0, true) ?: -1) >= 0, "exception empty")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(false, "exception empty")
        } catch (e: Throwable) {
            assertTrue(false, "exception empty. ${e.message}")
        }
    }
}
