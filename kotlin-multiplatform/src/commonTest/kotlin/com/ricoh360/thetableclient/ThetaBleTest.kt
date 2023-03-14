package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.MockBleScanner
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.*

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
}
