package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.MockBleScanner
import com.ricoh360.thetableclient.ble.newAdvertisement
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

class ThetaBleTest {

    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call ThetaBle.scan.
     */
    @Test
    fun scanTest() = runTest {
        val device = ThetaBle.scan(devName)
        assertNotNull(device, "Scan device")
        assertEquals(device.name, devName, "Scan device")
        assertNull(device.uuid, "Scan device")
        assertEquals(device.timeout.timeoutScan, TIMEOUT_SCAN, "Timeout of scan")
        assertEquals(device.timeout.timeoutPeripheral, TIMEOUT_PERIPHERAL, "Timeout of peripheral")
        assertEquals(device.timeout.timeoutConnect, TIMEOUT_CONNECT, "Timeout of connect")
        assertEquals(
            device.timeout.timeoutTakePicture,
            TIMEOUT_TAKE_PICTURE,
            "Timeout of take picture"
        )
    }

    /**
     * Timeout for ThetaBle.scan call.
     */
    @Test
    fun setTimeoutTest() = runTest {
        val timeoutScan = 100
        val timeoutPeripheral = 200
        val timeoutConnect = 300
        val timeoutTakePicture = 400
        val timeout = ThetaBle.Timeout(
            timeoutScan = timeoutScan,
            timeoutPeripheral = timeoutPeripheral,
            timeoutConnect = timeoutConnect,
            timeoutTakePicture = timeoutTakePicture,
        )
        val device = ThetaBle.scan(devName, timeout)
        assertNotNull(device, "Scan device")
        assertEquals(device.timeout.timeoutScan, timeoutScan, "Timeout of scan")
        assertEquals(device.timeout.timeoutPeripheral, timeoutPeripheral, "Timeout of peripheral")
        assertEquals(device.timeout.timeoutConnect, timeoutConnect, "Timeout of connect")
        assertEquals(
            device.timeout.timeoutTakePicture,
            timeoutTakePicture,
            "Timeout of take picture"
        )
    }

    /**
     * Timeout of scan for ThetaBle.scan call.
     */
    @Test
    fun setScanTimeoutTest() = runTest {
        val timeoutScan = 100
        val timeout = ThetaBle.Timeout(
            timeoutScan = timeoutScan,
        )
        val device = ThetaBle.scan(devName, timeout)
        assertNotNull(device, "Scan device")
        assertEquals(device.timeout.timeoutScan, timeoutScan, "Timeout of scan")
        assertEquals(device.timeout.timeoutPeripheral, TIMEOUT_PERIPHERAL, "Timeout of peripheral")
        assertEquals(device.timeout.timeoutConnect, TIMEOUT_CONNECT, "Timeout of connect")
        assertEquals(
            device.timeout.timeoutTakePicture,
            TIMEOUT_TAKE_PICTURE,
            "Timeout of take picture"
        )
    }

    /**
     * Timeout of peripheral for ThetaBle.scan call.
     */
    @Test
    fun setPeripheralTimeoutTest() = runTest {
        val timeoutPeripheral = 200
        val timeout = ThetaBle.Timeout(
            timeoutPeripheral = timeoutPeripheral,
        )
        val device = ThetaBle.scan(devName, timeout)
        assertNotNull(device, "Scan device")
        assertEquals(device.timeout.timeoutScan, TIMEOUT_SCAN, "Timeout of scan")
        assertEquals(device.timeout.timeoutPeripheral, timeoutPeripheral, "Timeout of peripheral")
        assertEquals(device.timeout.timeoutConnect, TIMEOUT_CONNECT, "Timeout of connect")
        assertEquals(
            device.timeout.timeoutTakePicture,
            TIMEOUT_TAKE_PICTURE,
            "Timeout of take picture"
        )
    }

    /**
     * Timeout of connect for ThetaBle.scan call.
     */
    @Test
    fun setConnectTimeoutTest() = runTest {
        val timeoutConnect = 300
        val timeout = ThetaBle.Timeout(
            timeoutConnect = timeoutConnect,
        )
        val device = ThetaBle.scan(devName, timeout)
        assertNotNull(device, "Scan device")
        assertEquals(device.timeout.timeoutScan, TIMEOUT_SCAN, "Timeout of scan")
        assertEquals(device.timeout.timeoutPeripheral, TIMEOUT_PERIPHERAL, "Timeout of peripheral")
        assertEquals(device.timeout.timeoutConnect, timeoutConnect, "Timeout of connect")
        assertEquals(
            device.timeout.timeoutTakePicture,
            TIMEOUT_TAKE_PICTURE,
            "Timeout of take picture"
        )
    }

    /**
     * Timeout of take picture for ThetaBle.scan call.
     */
    @Test
    fun setTakePictureTimeoutTest() = runTest {
        val timeoutTakePicture = 400
        val timeout = ThetaBle.Timeout(
            timeoutTakePicture = timeoutTakePicture,
        )
        val device = ThetaBle.scan(devName, timeout)
        assertNotNull(device, "Scan device")
        assertEquals(device.timeout.timeoutScan, TIMEOUT_SCAN, "Timeout of scan")
        assertEquals(device.timeout.timeoutPeripheral, TIMEOUT_PERIPHERAL, "Timeout of peripheral")
        assertEquals(device.timeout.timeoutConnect, TIMEOUT_CONNECT, "Timeout of connect")
        assertEquals(
            device.timeout.timeoutTakePicture,
            timeoutTakePicture,
            "Timeout of take picture"
        )
    }

    /**
     * Timeout for ThetaBle.scan call.
     */
    @Test
    fun scanTimeoutTest() = runTest {
        MockBleScanner.scanInterval = 200

        val timeoutScan = 100
        val timeout = ThetaBle.Timeout(
            timeoutScan = timeoutScan,
        )
        val device = ThetaBle.scan(devName, timeout)
        assertNull(device, "Scan device")
    }

    /**
     * Exception for ThetaBle.scan of init call.
     */
    @Test
    fun scanExceptionInitTest() = runTest {
        MockBleScanner.onInit = {
            throw Exception("init")
        }

        try {
            ThetaBle.scan(devName)
            assertTrue(false, "exception scan init")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("init", 0, true) >= 0, "exception scan init")
        } catch (e: Throwable) {
            e.printStackTrace()
            assertTrue(false, "exception scan init")
        }
    }

    /**
     * Exception bluetooth for ThetaBle.scan of scan call.
     */
    @Test
    fun scanExceptionBleScanTest() = runTest {
        MockBleScanner.bleList = listOf(null)

        try {
            ThetaBle.scan(devName)
            assertTrue(false, "exception scan")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("scan", 0, true) >= 0, "exception scan")
        } catch (e: Throwable) {
            assertTrue(false, "exception scan")
        }
    }

    /**
     * Exception api for ThetaBle.scan of scan call.
     */
    @Test
    fun scanExceptionApiScanTest() = runTest {
        MockBleScanner.bleList = listOf(null)

        try {
            ThetaBle.scan(devName)
            assertTrue(false, "exception scan")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("scan", 0, true) >= 0, "exception scan")
        } catch (e: Throwable) {
            assertTrue(false, "exception scan")
        }
    }

    /**
     * Exception api for ThetaBle.scan of nearby scan call.
     */
    @Test
    fun scanExceptionApiScanNearbyTest() = runTest {
        MockBleScanner.bleList = listOf(null)

        try {
            ThetaBle.scan()
            assertTrue(false, "exception scan")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("scan", 0, true) >= 0, "exception scan")
        } catch (e: Throwable) {
            assertTrue(false, "exception scan")
        }
    }

    /**
     * First wait for ThetaBle.scan call.
     */
    @Test
    fun scanFirstWaitTest() = runBlocking {
        val startTime = getSystemTimeMillis()
        assertEquals(ThetaBle.waitScan, WAIT_SCAN, "First wait")

        val deviceFirst = ThetaBle.scan(devName)

        assertNotNull(deviceFirst, "First wait")
        assertEquals(ThetaBle.waitScan, 0, "First wait")
        val firstTime = getSystemTimeMillis()
        assertTrue(
            firstTime - startTime > WAIT_SCAN,
            "First wait: ${firstTime - startTime} ${ThetaBle.waitScan}"
        )

        val deviceSecond = ThetaBle.scan(devName)
        assertNotNull(deviceSecond, "First wait")
        val secondTime = getSystemTimeMillis()
        assertTrue(secondTime - firstTime < 100, "First wait: ${secondTime - firstTime}")
    }

    /**
     * call ThetaBle.scan of nearby scan.
     */
    @Test
    fun scanSsid() = runTest {
        val nameList = listOf(
            "12345678",
            "23456789",
            "AB12345678",
            "A3456789",
            "123456789",
            "1234567",
            "12345678AB",
            "A12345678B",
            "ab12345678",
        )
        MockBleScanner.bleList = nameList

        val thetaList = ThetaBle.scan()
        assertEquals(thetaList.size, 3)
        assertEquals(thetaList[0].name, nameList[0])
        assertEquals(thetaList[1].name, nameList[1])
        assertEquals(thetaList[2].name, nameList[2])
    }

    /**
     * call ThetaBle.scanThetaSsid.
     */
    @Test
    fun scanThetaSsid() = runTest {
        val nameList = listOf(
            "12345678",
        )
        MockBleScanner.bleList = nameList

        val ssidListAll = ThetaBle.scanThetaSsid()
        assertEquals(ssidListAll.size, 5)
        assertEquals(ssidListAll[0].second, nameList[0])
        assertEquals(ssidListAll[1].second, nameList[0])
        assertEquals(ssidListAll[2].second, nameList[0])
        assertEquals(ssidListAll[3].second, nameList[0])
        assertEquals(ssidListAll[4].second, nameList[0])

        assertEquals(ssidListAll[0].first, "THETAAA${nameList[0]}.OSC")
        assertEquals(ssidListAll[1].first, "THETAYR${nameList[0]}.OSC")
        assertEquals(ssidListAll[2].first, "THETAYN${nameList[0]}.OSC")
        assertEquals(ssidListAll[3].first, "THETAYP${nameList[0]}.OSC")
        assertEquals(ssidListAll[4].first, "THETAYL${nameList[0]}.OSC")
    }

    /**
     * call ThetaBle.scanThetaSsid.
     */
    @Test
    fun scanThetaSsidWithModelX() = runTest {
        val nameList = listOf(
            "12345678",
        )
        MockBleScanner.bleList = nameList

        val ssidListAll = ThetaBle.scanThetaSsid(ThetaModel.THETA_X)
        assertEquals(ssidListAll.size, 1)
        assertEquals(ssidListAll[0].second, nameList[0])
        assertEquals(ssidListAll[0].first, "THETAYR${nameList[0]}.OSC")
    }

    /**
     * call ThetaBle.scanThetaSsid.
     */
    @Test
    fun scanThetaSsidWithModelA1() = runTest {
        val nameList = listOf(
            "AA12345678",
            "1234"
        )
        MockBleScanner.bleList = nameList

        val ssidListAll = ThetaBle.scanThetaSsid()
        assertEquals(ssidListAll.size, 1)
        assertEquals(ssidListAll[0].second, nameList[0].takeLast(8))
        assertEquals(ssidListAll[0].first, "THETAAA${nameList[0].takeLast(8)}.OSC")
    }

    /**
     * Exception unsupported model for ThetaBle.scanThetaSsid.
     */
    @Test
    fun scanThetaSsidWithUnsupportedModel() = runTest {
        val nameList = listOf(
            "12345678",
        )
        MockBleScanner.bleList = nameList

        try {
            ThetaBle.scanThetaSsid(ThetaModel.THETA_S)
            assertTrue(false)
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Unsupported", 0, true) >= 0, "exception getSsid")
        }
    }

    /**
     * Exception bluetooth for ThetaBle.scanThetaSsid.
     */
    @Test
    fun scanThetaSsidExceptionApiScanNearbyTest() = runTest {
        MockBleScanner.bleList = listOf(null)

        try {
            ThetaBle.scanThetaSsid()
            assertTrue(false, "exception scan")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("scan", 0, true) >= 0, "exception scan")
        } catch (e: Throwable) {
            assertTrue(false, "exception scan")
        }
    }

    /**
     * call ThetaDevice.getSsid.
     */
    @Test
    fun deviceGetSsid() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val nameList = listOf(
            Pair(ThetaModel.THETA_A1, "THETAAA$devName.OSC"),
            Pair(ThetaModel.THETA_V, "THETAYL$devName.OSC"),
            Pair(ThetaModel.THETA_Z1, "THETAYN$devName.OSC"),
            Pair(ThetaModel.THETA_SC2, "THETAYP$devName.OSC"),
            Pair(ThetaModel.THETA_X, "THETAYR$devName.OSC"),
            Pair(ThetaModel.THETA_SC2_B, "THETAYP$devName.OSC"),
            Pair(ThetaModel.THETA_S, null),
            Pair(ThetaModel.THETA_SC, null),
            Pair(ThetaModel.UNKNOWN, null),
        )
        nameList.forEach {
            try {
                val ssid = device.getSsid(it.first)
                assertEquals(ssid.first, it.second)
                assertEquals(ssid.second, devName)
            } catch (e: ThetaBle.ThetaBleApiException) {
                assertTrue(e.message!!.indexOf("Unsupported", 0, true) >= 0, "exception getSsid")
                assertNull(it.second)
            } catch (e: Throwable) {
                assertTrue(false, "exception getSsid2 ${it.first.name}")
            }
        }
    }

    /**
     * call ThetaDevice.getSsid.
     */
    @Test
    fun deviceGetSsidForName() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        val nameList = listOf(
            Pair(ThetaModel.THETA_A1, "THETAAA$devName.OSC"),
            Pair(ThetaModel.THETA_V, "THETAYL$devName.OSC"),
            Pair(ThetaModel.THETA_Z1, "THETAYN$devName.OSC"),
            Pair(ThetaModel.THETA_SC2, "THETAYP$devName.OSC"),
            Pair(ThetaModel.THETA_X, "THETAYR$devName.OSC"),
            Pair(ThetaModel.THETA_SC2_B, "THETAYP$devName.OSC"),
            Pair(ThetaModel.THETA_S, null),
            Pair(ThetaModel.THETA_SC, null),
            Pair(ThetaModel.UNKNOWN, null),
        )
        nameList.forEach {
            try {
                val ssid = device.getSsid(it.first)
                assertEquals(ssid.first, it.second)
                assertEquals(ssid.second, devName)
            } catch (e: ThetaBle.ThetaBleApiException) {
                assertTrue(e.message!!.indexOf("Unsupported", 0, true) >= 0, "exception getSsid")
                assertNull(it.second)
            } catch (e: Throwable) {
                assertTrue(false, "exception getSsid2 ${it.first.name}")
            }
        }
    }

    /**
     * check ThetaDevice.model.
     */
    @Test
    fun deviceModel() = runTest {
        val nameList = listOf(
            Pair("AA12345678", ThetaModel.THETA_A1),
            Pair("12345678", null),
            Pair("aa12345678", null),
            Pair("AB12345678", null),
        )
        nameList.forEach {
            val device = ThetaBle.ThetaDevice(newAdvertisement(it.first))
            assertEquals(device.name, it.first)
            assertEquals(device.model, it.second)
        }
    }

}
