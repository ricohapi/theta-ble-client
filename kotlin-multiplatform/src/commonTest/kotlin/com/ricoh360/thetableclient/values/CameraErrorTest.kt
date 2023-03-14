package com.ricoh360.thetableclient.values

import com.ricoh360.thetableclient.*
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.values.CameraError
import com.ricoh360.thetableclient.transferred.CameraState
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class CameraErrorTest {

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * Convert CameraError.
     */
    @Test
    fun convertTest() = runTest {
        val dataList = listOf(
            Pair(
                CameraState(cameraError = arrayListOf("NO_MEMORY")),
                arrayListOf(CameraError.NO_MEMORY)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("FILE_NUMBER_OVER")),
                arrayListOf(CameraError.FILE_NUMBER_OVER)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("NO_DATE_SETTING")),
                arrayListOf(CameraError.NO_DATE_SETTING)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("READ_ERROR")),
                arrayListOf(CameraError.READ_ERROR)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("NOT_SUPPORTED_MEDIA_TYPE")),
                arrayListOf(CameraError.NOT_SUPPORTED_MEDIA_TYPE)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("NOT_SUPPORTED_FILE_SYSTEM")),
                arrayListOf(CameraError.NOT_SUPPORTED_FILE_SYSTEM)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("MEDIA_NOT_READY")),
                arrayListOf(CameraError.MEDIA_NOT_READY)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("NOT_ENOUGH_BATTERY")),
                arrayListOf(CameraError.NOT_ENOUGH_BATTERY)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("INVALID_FILE")),
                arrayListOf(CameraError.INVALID_FILE)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("PLUGIN_BOOT_ERROR")),
                arrayListOf(CameraError.PLUGIN_BOOT_ERROR)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("IN_PROGRESS_ERROR")),
                arrayListOf(CameraError.IN_PROGRESS_ERROR)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("CANNOT_RECORDING")),
                arrayListOf(CameraError.CANNOT_RECORDING)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("CANNOT_RECORD_LOWBAT")),
                arrayListOf(CameraError.CANNOT_RECORD_LOWBAT)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("CAPTURE_HW_FAILED")),
                arrayListOf(CameraError.CAPTURE_HW_FAILED)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("CAPTURE_SW_FAILED")),
                arrayListOf(CameraError.CAPTURE_SW_FAILED)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("INTERNAL_MEM_ACCESS_FAIL")),
                arrayListOf(CameraError.INTERNAL_MEM_ACCESS_FAIL)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("UNEXPECTED_ERROR")),
                arrayListOf(CameraError.UNEXPECTED_ERROR)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("BATTERY_CHARGE_FAIL")),
                arrayListOf(CameraError.BATTERY_CHARGE_FAIL)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("HIGH_TEMPERATURE_WARNING")),
                arrayListOf(CameraError.HIGH_TEMPERATURE_WARNING)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("HIGH_TEMPERATURE")),
                arrayListOf(CameraError.HIGH_TEMPERATURE)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("BATTERY_HIGH_TEMPERATURE")),
                arrayListOf(CameraError.BATTERY_HIGH_TEMPERATURE)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("COMPASS_CALIBRATION")),
                arrayListOf(CameraError.COMPASS_CALIBRATION)
            ),
            Pair(
                CameraState(cameraError = arrayListOf("undefined value")),
                arrayListOf(CameraError.UNKNOWN)
            ),
            Pair(CameraState(cameraError = arrayListOf()), arrayListOf()),
            Pair(CameraState(cameraError = null), null),
        )
        assertEquals(CameraError.values().size + 2, dataList.size)
        dataList.forEach {
            assertEquals(ThetaState(it.first).cameraError, it.second)
        }
    }
}
