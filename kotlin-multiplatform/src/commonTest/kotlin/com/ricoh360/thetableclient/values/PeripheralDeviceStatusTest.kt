package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.PeripheralDeviceStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PeripheralDeviceStatusTest {
    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(PeripheralDeviceStatus.UNKNOWN, "invalid value"),
            Pair(PeripheralDeviceStatus.IDLE, "idle"),
            Pair(PeripheralDeviceStatus.CONNECTED, "connected"),
        )
        assertEquals(PeripheralDeviceStatus.entries.size, dataList.size)
        dataList.forEach {
            assertEquals(it.first, PeripheralDeviceStatus.getFromValue(it.second))
        }
    }
}
