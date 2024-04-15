package com.ricoh360.thetableclient.shootingcontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.FileFormat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetFileFormatTest {
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
     * call setFileFormat.
     */
    @Test
    fun normalTest() = runBlocking {
        val enumList = listOf(
            Pair(0, FileFormat.IMAGE_5K),
            Pair(1, FileFormat.VIDEO_4K),
            Pair(3, FileFormat.VIDEO_2K),
            Pair(6, FileFormat.IMAGE_6_7K),
            Pair(7, FileFormat.RAW_P_6_7K),
            Pair(9, FileFormat.VIDEO_2_7K),
            Pair(11, FileFormat.VIDEO_3_6K),
        )
        enumList.forEach {
            setFileFormatTest(it.first.toByte(), it.second)
        }
    }

    private fun setFileFormatTest(bleData: Byte, fileFormat: FileFormat) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        println("setFileFormatTest: $fileFormat")

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "FILE_FORMAT") {
                assertEquals(data[0], bleData, "setFileFormat")
                deferred.complete(Unit)
            }
        }
        device.shootingControlCommand?.setFileFormat(fileFormat)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setFileFormat")
    }

    /**
     * Not connected exception for setFileFormat call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val shootingControlCommand = device.shootingControlCommand
            device.disconnect()
            shootingControlCommand?.setFileFormat(FileFormat.IMAGE_5K)
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
     * Read exception for setFileFormat call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val fileFormat = FileFormat.IMAGE_5K
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "FILE_FORMAT") {
                assertEquals(data[0], fileFormat.ble, "setFileFormat")
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.shootingControlCommand?.setFileFormat(fileFormat)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }

    /**
     * Exception set reserved for setFileFormat call.
     */
    @Test
    fun setReservedTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val fileFormat = FileFormat.RESERVED
        MockBlePeripheral.onWrite = { _, _ ->
            assertTrue(false, "onWrite")
        }

        try {
            device.connect()
            device.shootingControlCommand?.setFileFormat(fileFormat)
            assertTrue(false, "set reserved")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(e.message!!.indexOf("Reserved value", 0, true) >= 0, "set reserved")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
