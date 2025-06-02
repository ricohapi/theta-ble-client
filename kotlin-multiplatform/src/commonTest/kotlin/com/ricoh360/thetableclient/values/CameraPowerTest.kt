package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.OptionName
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CameraPowerTest {
    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * CameraPower.
     */
    @Test
    fun checkBle() = runBlocking {
        val values = listOf(
            Pair(CameraPower.UNKNOWN, null),
            Pair(CameraPower.OFF, 0),
            Pair(CameraPower.ON, 1),
            Pair(CameraPower.SLEEP, 2),
            Pair(CameraPower.POWER_SAVING, null),
            Pair(CameraPower.SILENT_MODE, null),
        )
        assertEquals(values.size, CameraPower.entries.size)
        values.forEach {
            assertEquals(it.first.ble, it.second?.toByte(), "CameraPower ${it.first}")
        }
    }

    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(CameraPower.UNKNOWN, "invalid value"),
            Pair(CameraPower.OFF, "off"),
            Pair(CameraPower.ON, "on"),
            Pair(CameraPower.SLEEP, "sleep"),
            Pair(CameraPower.POWER_SAVING, "powerSaving"),
            Pair(CameraPower.SILENT_MODE, "silentMode"),
        )
        assertEquals(CameraPower.entries.size, dataList.size)
        dataList.forEach {
            assertEquals(it.first, CameraPower.getFromValue(it.second))
        }
    }

    @Test
    fun setOptionValue() = runTest {
        val options = ThetaOptions()
        options.setValue(OptionName.CameraPower, CameraPower.POWER_SAVING)
        assertEquals(options.cameraPower, CameraPower.POWER_SAVING)
        assertEquals(options.getValue(OptionName.CameraPower), CameraPower.POWER_SAVING)
    }
}
