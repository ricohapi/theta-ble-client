package com.ricoh360.thetableclient.shootingcontrol

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ValuesTest {
    @BeforeTest
    fun setup() {
        println("setup")
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * CaptureMode.
     */
    @Test
    fun checkCaptureMode() = runBlocking {
        val values = listOf(
            Pair(CaptureMode.IMAGE, 0),
            Pair(CaptureMode.VIDEO, 2),
            Pair(CaptureMode.LIVE, 3),
        )
        assertEquals(values.size, CaptureMode.values().size)
        values.forEach {
            assertEquals(it.first.ble, it.second?.toByte(), "CaptureMode ${it.first}")
        }
    }
}
