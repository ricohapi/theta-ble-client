package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.values.CaptureStatus
import com.ricoh360.thetableclient.transferred.CameraState
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CaptureStatusTest {

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * Convert CaptureStatus.
     */
    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(CameraState(captureStatus = "shooting"), CaptureStatus.SHOOTING),
            Pair(CameraState(captureStatus = "idle"), CaptureStatus.IDLE),
            Pair(
                CameraState(captureStatus = "self-timer countdown"),
                CaptureStatus.SELF_TIMER_COUNTDOWN
            ),
            Pair(CameraState(captureStatus = "bracket shooting"), CaptureStatus.BRACKET_SHOOTING),
            Pair(CameraState(captureStatus = "converting"), CaptureStatus.CONVERTING),
            Pair(
                CameraState(captureStatus = "timeShift shooting"),
                CaptureStatus.TIME_SHIFT_SHOOTING
            ),
            Pair(
                CameraState(captureStatus = "continuous shooting"),
                CaptureStatus.CONTINUOUS_SHOOTING
            ),
            Pair(
                CameraState(captureStatus = "retrospective image recording"),
                CaptureStatus.RETROSPECTIVE_IMAGE_RECORDING
            ),
            Pair(CameraState(captureStatus = "undefined value"), CaptureStatus.UNKNOWN),
            Pair(CameraState(captureStatus = null), null),
        )
        assertEquals(CaptureStatus.values().size + 1, dataList.size)
        dataList.forEach {
            assertEquals(ThetaState(it.first).captureStatus, it.second, it.second?.name)
        }
    }
}
