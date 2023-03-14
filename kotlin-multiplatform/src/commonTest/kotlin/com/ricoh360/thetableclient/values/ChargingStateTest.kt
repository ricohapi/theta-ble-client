package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.transferred.CameraState
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ChargingStateTest {

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * Convert ChargingState.
     */
    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(CameraState(batteryState = "charging"), ChargingState.CHARGING),
            Pair(CameraState(batteryState = "charged"), ChargingState.CHARGED),
            Pair(CameraState(batteryState = "disconnect"), ChargingState.DISCONNECT),
            Pair(CameraState(batteryState = "undefined value"), ChargingState.UNKNOWN),
            Pair(CameraState(batteryState = null), null),
        )
        assertEquals(ChargingState.values().size + 1, dataList.size)
        dataList.forEach {
            assertEquals(ThetaState(it.first).batteryState, it.second, it.second?.name)
        }
    }
}
