package com.ricoh360.thetableclient.cameracontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.OptionName
import com.ricoh360.thetableclient.service.data.values.WlanAntennaConfig
import com.ricoh360.thetableclient.service.data.values.WlanFrequency
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetOptionsTest {
    private val devName = "AA12345678"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * getOptions.
     */
    @Test
    fun getOptionsTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText =
            Resource("src/commonTest/resources/cameracontrolv2/getOptions/options.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_OPTIONS)
            jsonText.encodeToByteArray()
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            assertEquals(characteristic, BleCharacteristic.GET_OPTIONS)
            val jsonString = data.decodeToString()
            assertTrue(
                (jsonString.indexOf("_accessInfo", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("cameraPower", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("captureMode", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("defaultWifiPassword", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("networkType", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("password", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("ssid", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("username", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("wlanAntennaConfig", 0, true)) >= 0,
                "exception getOptions"
            )
            assertTrue(
                (jsonString.indexOf("wlanFrequency", 0, true)) >= 0,
                "exception getOptions"
            )
        }

        val options = service.getOptions(
            listOf(
                OptionName.AccessInfo,
                OptionName.CameraPower,
                OptionName.CaptureMode,
                OptionName.DefaultWifiPassword,
                OptionName.NetworkType,
                OptionName.Password,
                OptionName.Ssid,
                OptionName.Username,
                OptionName.WlanAntennaConfig,
                OptionName.WlanFrequency,
            )
        )
        assertEquals(options.cameraPower, CameraPower.ON)
        assertEquals(options.captureMode, CaptureMode.IMAGE)
        assertEquals(options.defaultWifiPassword, "10010011")
        assertEquals(options.networkType, NetworkType.DIRECT)
        assertEquals(options.password, "password123")
        assertEquals(options.ssid, "ssid123")
        assertEquals(options.username, "adminUser")
        assertEquals(options.wlanAntennaConfig, WlanAntennaConfig.MIMO)
        assertEquals(options.wlanFrequency, WlanFrequency.GHZ_2_4)

        val accessInfo = options.accessInfo
        assertNotNull(accessInfo)
        assertEquals(accessInfo.ssid, "test_ssid")
        assertEquals(accessInfo.ipAddress, "10.0.0.222")
        assertEquals(accessInfo.subnetMask, "255.255.255.0")
        assertEquals(accessInfo.defaultGateway, "10.0.0.2")
        assertEquals(accessInfo.proxyURL, "proxy-url")
        assertEquals(accessInfo.frequency, WlanFrequency.GHZ_2_4)
        assertEquals(accessInfo.wlanSignalStrength, -51)
        assertEquals(accessInfo.wlanSignalLevel, 4)
        assertEquals(accessInfo.lteSignalStrength, 0)
        assertEquals(accessInfo.lteSignalLevel, 0)
        assertEquals(accessInfo.dhcpLeaseAddress?.size, 1)
        val dhcpLeaseAddress = accessInfo.dhcpLeaseAddress?.get(0)
        assertNotNull(dhcpLeaseAddress)
        assertEquals(dhcpLeaseAddress.ipAddress, "10.0.0.3")
        assertEquals(dhcpLeaseAddress.macAddress, "58:38:79:9f:00:00")
        assertEquals(dhcpLeaseAddress.hostName, "host_name")
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
            service.getOptions(listOf(OptionName.CaptureMode))
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
            service.getOptions(listOf(OptionName.CaptureMode))
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
            service.getOptions(listOf(OptionName.CaptureMode))
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
            service.getOptions(listOf(OptionName.CaptureMode))
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
            service.getOptions(listOf(OptionName.CaptureMode))
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
            service.getOptions(listOf(OptionName.CaptureMode))
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
