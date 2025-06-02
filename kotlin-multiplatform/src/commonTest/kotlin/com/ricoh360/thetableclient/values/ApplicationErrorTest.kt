package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.ApplicationError
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationErrorTest {
    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * CaptureMode.
     */
    @Test
    fun checkBle() = runBlocking {
        val values = listOf(
            Pair(ApplicationError.UNKNOWN, null),
            Pair(ApplicationError.DISABLE_COMMAND, 0x80),
            Pair(ApplicationError.MISSING_PARAMETER, 0x81),
            Pair(ApplicationError.INVALID_PARAMETER_VALUE, 0x82),
            Pair(ApplicationError.POWER_OFF_SEQUENCE_RUNNING, 0x83),
            Pair(ApplicationError.INVALID_FILE_FORMAT, 0x84),
            Pair(ApplicationError.SERVICE_UNAVAILABLE, 0x85),
            Pair(ApplicationError.DEVICE_BUSY, 0x86),
            Pair(ApplicationError.UNEXPECTED, 0x87),
            Pair(ApplicationError.UNKNOWN_COMMAND, 0x88),
            Pair(ApplicationError.TOO_MANY_PARAMETERS, 0x89),
            Pair(ApplicationError.NO_FREE_SPACE, 0x8A),
            Pair(ApplicationError.CANCELED_SHOOTING, 0x8B),
            Pair(ApplicationError.SIZE_OVER, 0x8C),
        )
        assertEquals(values.size, ApplicationError.entries.size)
        values.forEach {
            assertEquals(it.first.ble, it.second?.toByte(), "ApplicationError ${it.first}")
        }
    }

    @Test
    fun checkErrorResponse() = runTest {
        val dataList = listOf(
            Pair(0x79, null),
            Pair(0x80, ApplicationError.DISABLE_COMMAND),
            Pair(0x81, ApplicationError.MISSING_PARAMETER),
            Pair(0x8C, ApplicationError.SIZE_OVER),
            Pair(0x8D, ApplicationError.UNKNOWN),
            Pair(0x9F, ApplicationError.UNKNOWN),
            Pair(0xA0, null),
        )
        dataList.forEach {
            val data = ByteArray(2)
            data[0] = it.first.toByte()
            assertEquals(ApplicationError.checkErrorResponse(data), it.second, "${it.second}")
        }
        assertEquals(ApplicationError.checkErrorResponse(ByteArray(0)), null, "empty")
    }
}
