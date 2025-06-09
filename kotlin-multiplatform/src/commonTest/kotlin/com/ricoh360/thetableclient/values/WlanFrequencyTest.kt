package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.OptionName
import com.ricoh360.thetableclient.service.data.values.WlanFrequency
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WlanFrequencyTest {
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
            Pair(WlanFrequency.UNKNOWN, 0.0),
            Pair(WlanFrequency.GHZ_2_4, 2.4),
            Pair(WlanFrequency.GHZ_5, 5.0),
            Pair(WlanFrequency.GHZ_5_2, 5.2),
            Pair(WlanFrequency.GHZ_5_8, 5.8),
            Pair(WlanFrequency.INITIAL_VALUE, null),
        )
        assertEquals(WlanFrequency.entries.size, dataList.size)
        dataList.forEach {
            it.second?.let { value ->
                assertEquals(it.first, WlanFrequency.getFromValue(value))
            }
        }
    }

    @Test
    fun setOptionValue() = runTest {
        val options = ThetaOptions()
        options.setValue(OptionName.WlanFrequency, WlanFrequency.GHZ_5)
        assertEquals(options.wlanFrequency, WlanFrequency.GHZ_5)
        assertEquals(options.getValue(OptionName.WlanFrequency), WlanFrequency.GHZ_5)
    }
}
