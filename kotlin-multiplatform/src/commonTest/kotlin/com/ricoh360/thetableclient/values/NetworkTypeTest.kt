package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.OptionName
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkTypeTest {
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
            Pair(NetworkType.UNKNOWN, "invalid value"),
            Pair(NetworkType.OFF, "OFF"),
            Pair(NetworkType.DIRECT, "AP"),
            Pair(NetworkType.CLIENT, "CL"),
            Pair(NetworkType.ETHERNET, "ETHERNET"),
            Pair(NetworkType.LTE_PLAN_D, "LTE plan-D"),
            Pair(NetworkType.LTE_PLAN_DU, "LTE plan-DU"),
            Pair(NetworkType.LTE_PLAN_01S, "LTE plan01s"),
            Pair(NetworkType.LTE_PLAN_X3, "LTE planX3"),
            Pair(NetworkType.LTE_PLAN_P1, "LTE planP1"),
            Pair(NetworkType.LTE_PLAN_K2, "LTE plan-K2"),
            Pair(NetworkType.LTE_PLAN_K, "LTE plan-K"),
            Pair(NetworkType.SCAN, "SCAN"),
        )
        assertEquals(NetworkType.entries.size, dataList.size)
        dataList.forEach {
            assertEquals(it.first, NetworkType.getFromValue(it.second))
        }
    }

    @Test
    fun setOptionValue() = runTest {
        val options = ThetaOptions()
        options.setValue(OptionName.NetworkType, NetworkType.DIRECT)
        assertEquals(options.networkType, NetworkType.DIRECT)
        assertEquals(options.getValue(OptionName.NetworkType), NetworkType.DIRECT)
    }
}
