package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.OptionName
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptureModeTest {
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
            Pair(CaptureMode.UNKNOWN, null),
            Pair(CaptureMode.IMAGE, 0),
            Pair(CaptureMode.VIDEO, 2),
            Pair(CaptureMode.LIVE, 3),
            Pair(CaptureMode.INTERVAL, null),
            Pair(CaptureMode.PRESET, null),
            Pair(CaptureMode.WEB_RTC, null),
        )
        assertEquals(values.size, CaptureMode.entries.size)
        values.forEach {
            assertEquals(it.first.ble, it.second?.toByte(), "CaptureMode ${it.first}")
        }
    }

    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(CaptureMode.UNKNOWN, "invalid value"),
            Pair(CaptureMode.IMAGE, "image"),
            Pair(CaptureMode.VIDEO, "video"),
            Pair(CaptureMode.LIVE, "_liveStreaming"),
            Pair(CaptureMode.INTERVAL, "interval"),
            Pair(CaptureMode.PRESET, "_preset"),
            Pair(CaptureMode.WEB_RTC, "_streaming"),
        )
        assertEquals(CaptureMode.entries.size, dataList.size)
        dataList.forEach {
            assertEquals(it.first, CaptureMode.getFromValue(it.second))
        }
    }

    @Test
    fun setOptionValue() = runTest {
        val options = ThetaOptions()
        options.setValue(OptionName.CaptureMode, CaptureMode.IMAGE)
        assertEquals(options.captureMode, CaptureMode.IMAGE)
        assertEquals(options.getValue(OptionName.CaptureMode), CaptureMode.IMAGE)
    }
}
