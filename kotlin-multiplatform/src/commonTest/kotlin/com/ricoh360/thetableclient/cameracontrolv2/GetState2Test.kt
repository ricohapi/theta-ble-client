package com.ricoh360.thetableclient.cameracontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class GetState2Test {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call getState2.
     */
    @Test
    fun getState2Test() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText = Resource("src/commonTest/resources/cameracontrolv2/getState2/state2.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_STATE2)
            jsonText.encodeToByteArray()
        }

        val thetaState2 = service.getState2()
        assertNotNull(thetaState2.externalGpsInfo)
        assertEquals(thetaState2.externalGpsInfo?.gpsInfo?.altitude, 4.99F)
        assertEquals(thetaState2.externalGpsInfo?.gpsInfo?.dateTimeZone, "2023:08:09 16:23:16+09:00")
        assertEquals(thetaState2.externalGpsInfo?.gpsInfo?.datum, "WGS84")
        assertEquals(thetaState2.externalGpsInfo?.gpsInfo?.lat, 35.48F)
        assertEquals(thetaState2.externalGpsInfo?.gpsInfo?.lng, 134.24F)

        assertNotNull(thetaState2.internalGpsInfo)
        assertEquals(thetaState2.internalGpsInfo?.gpsInfo?.altitude, 5.99F)
        assertEquals(thetaState2.internalGpsInfo?.gpsInfo?.dateTimeZone, "2023:08:09 16:23:16+09:00")
        assertEquals(thetaState2.internalGpsInfo?.gpsInfo?.datum, "WGS84")
        assertEquals(thetaState2.internalGpsInfo?.gpsInfo?.lat, 36.48F)
        assertEquals(thetaState2.internalGpsInfo?.gpsInfo?.lng, 135.24F)
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
            assertEquals(it, BleCharacteristic.GET_STATE2)
            jsonText.encodeToByteArray()
        }

        try {
            service.getState2()
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
            service.getState2()
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
            service.getState2()
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
            service.getState2()
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
