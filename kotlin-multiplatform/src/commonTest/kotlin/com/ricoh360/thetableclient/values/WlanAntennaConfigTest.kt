package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.OptionName
import com.ricoh360.thetableclient.service.data.values.WlanAntennaConfig
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WlanAntennaConfigTest {
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
            Pair(WlanAntennaConfig.UNKNOWN, null),
            Pair(WlanAntennaConfig.SISO, "SISO"),
            Pair(WlanAntennaConfig.MIMO, "MIMO"),
        )
        assertEquals(WlanAntennaConfig.entries.size, dataList.size)
        dataList.forEach {
            assertEquals(it.first, WlanAntennaConfig.getFromValue(it.second))
        }
    }

    @Test
    fun setOptionValue() = runTest {
        val options = ThetaOptions()
        options.setValue(OptionName.WlanAntennaConfig, WlanAntennaConfig.MIMO)
        assertEquals(options.wlanAntennaConfig, WlanAntennaConfig.MIMO)
        assertEquals(options.getValue(OptionName.WlanAntennaConfig), WlanAntennaConfig.MIMO)
    }
}
