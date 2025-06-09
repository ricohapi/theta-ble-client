package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.values.ShootingFunction
import com.ricoh360.thetableclient.transferred.CameraState
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShootingFunctionTest {

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * Convert ShootingFunction.
     */
    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(CameraState(function = "normal"), ShootingFunction.NORMAL),
            Pair(CameraState(function = "selfTimer"), ShootingFunction.SELF_TIMER),
            Pair(CameraState(function = "mySetting"), ShootingFunction.MY_SETTING),
            Pair(CameraState(function = "undefined value"), ShootingFunction.UNKNOWN),
            Pair(CameraState(function = null), null),
        )
        assertEquals(ShootingFunction.values().size + 1, dataList.size)
        dataList.forEach {
            assertEquals(ThetaState(it.first).function, it.second, it.second?.name)
        }
    }
}
