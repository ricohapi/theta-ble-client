package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.service.data.ThetaInfo
import com.ricoh360.thetableclient.service.data.values.ThetaModel
import com.ricoh360.thetableclient.transferred.CameraInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ThetaModelTest {

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * Convert ThetaModel.
     */
    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(CameraInfo("", "RICOH THETA S", "", "", "", "", 0), ThetaModel.THETA_S),
            Pair(CameraInfo("", "RICOH THETA SC", "", "", "", "", 0), ThetaModel.THETA_SC),
            Pair(CameraInfo("", "RICOH THETA V", "", "", "", "", 0), ThetaModel.THETA_V),
            Pair(CameraInfo("", "RICOH THETA Z1", "", "", "", "", 0), ThetaModel.THETA_Z1),
            Pair(CameraInfo("", "RICOH THETA X", "", "", "", "", 0), ThetaModel.THETA_X),
            Pair(CameraInfo("", "RICOH THETA SC2", "", "", "", "", 0), ThetaModel.THETA_SC2),
            Pair(CameraInfo("", "RICOH THETA SC2", "4", "", "", "", 0), ThetaModel.THETA_SC2_B),
            Pair(CameraInfo("", "Other", "", "", "", "", 0), ThetaModel.UNKNOWN),
        )
        assertEquals(ThetaModel.values().size, dataList.size)
        dataList.forEach {
            assertEquals(ThetaInfo(it.first).model, it.second, it.second.name)
        }
    }
}
