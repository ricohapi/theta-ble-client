package com.ricoh360.thetableclient.shootingcontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.FileFormat
import com.ricoh360.thetableclient.toBytes
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFileFormatTest {
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
     * call getFileFormat.
     */
    @Test
    fun normalTest() = runBlocking {
        val enumList = listOf(
            Pair(0, FileFormat.IMAGE_5K),
            Pair(1, FileFormat.VIDEO_4K),
            Pair(2, FileFormat.RESERVED),
            Pair(3, FileFormat.VIDEO_2K),
            Pair(4, FileFormat.RESERVED),
            Pair(5, FileFormat.RESERVED),
            Pair(6, FileFormat.IMAGE_6_7K),
            Pair(7, FileFormat.RAW_P_6_7K),
            Pair(8, FileFormat.RESERVED),
            Pair(9, FileFormat.VIDEO_2_7K),
            Pair(10, FileFormat.RESERVED),
            Pair(11, FileFormat.VIDEO_3_6K),
        )
        enumList.forEach {
            getFileFormatTest(it.first.toByte(), it.second)
        }
    }

    private fun getFileFormatTest(bleData: Byte, fileFormat: FileFormat) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        println("getFileFormatTest: $fileFormat")
        MockBlePeripheral.onRead = {
            assertEquals(it.name, "FILE_FORMAT")
            bleData.toBytes()
        }
        val readValue = device.shootingControlCommand?.getFileFormat()
        assertEquals(readValue, fileFormat, "getFileFormat")
    }

    /**
     * Not connected exception for getFileFormat call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val shootingControlCommand = device.shootingControlCommand
            device.disconnect()
            shootingControlCommand?.getFileFormat()
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
     * Read exception for getFileFormat call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }

        try {
            device.connect()
            device.shootingControlCommand?.getFileFormat()
            assertTrue(false, "exception read")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("read", 0, true) >= 0, "exception read")
        } catch (e: Throwable) {
            assertTrue(false, "exception read. ${e.message}")
        }
    }

    /**
     * Read empty for getFileFormat call.
     */
    @Test
    fun emptyBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            ByteArray(0)
        }

        try {
            device.connect()
            device.shootingControlCommand?.getFileFormat()
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
     * Read unknown for getFileFormat call.
     */
    @Test
    fun unknownBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onRead = {
            20.toByte().toBytes()
        }

        try {
            device.connect()
            device.shootingControlCommand?.getFileFormat()
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
