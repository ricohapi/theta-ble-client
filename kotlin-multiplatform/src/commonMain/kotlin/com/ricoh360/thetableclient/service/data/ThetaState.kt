package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.service.data.values.CameraError
import com.ricoh360.thetableclient.service.data.values.CaptureStatus
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.ShootingFunction
import com.ricoh360.thetableclient.transferred.CameraState

/**
 * Mutable values representing THETA state
 */
data class ThetaState(
    /**
     * Battery level
     * (0.0 to 1.0)
     * When using an external power source, 1 (100%)
     */
    val batteryLevel: Float?,

    /**
     * Continuously shoots state
     */
    val captureStatus: CaptureStatus?,

    /**
     * Shooting time of movie (sec)
     */
    val recordedTime: Int?,

    /**
     * Remaining time of movie (sec)
     */
    val recordableTime: Int?,

    /**
     * Number of still images captured during continuous shooting, Unit: images
     */
    val capturedPictures: Int?,

    /**
     * URL of the last saved file
     *
     * Z1: http://[IP address]/files/[eMMC ID]/[Directory name]/[File name]
     * X: http://[IP address]/files/[Directory name]/[File name]
     * DNG format files are not displayed. For burst shooting, files in the DNG format are displayed.
     */
    val latestFileUrl: String?,

    /**
     * Charging state
     */
    val batteryState: ChargingState?,

    /**
     * Shooting function status
     */
    val function: ShootingFunction?,

    /**
     * Error information of the camera
     */
    val cameraError: List<CameraError>?,

    /**
     * true: Battery inserted; false: Battery not inserted
     *
     * RICOH THETA X or later
     */
    val batteryInsert: Boolean?,

    /**
     * Camera main board temperature
     */
    val boardTemp: Int?,

    /**
     * Battery temperature
     */
    val batteryTemp: Int?,
) {
    constructor() : this(
        batteryLevel = null,
        captureStatus = null,
        recordedTime = null,
        recordableTime = null,
        capturedPictures = null,
        latestFileUrl = null,
        batteryState = null,
        function = null,
        cameraError = null,
        batteryInsert = null,
        boardTemp = null,
        batteryTemp = null,
    )

    internal constructor(value: CameraState) : this(
        batteryLevel = value.batteryLevel,
        captureStatus = CaptureStatus.get(value.captureStatus),
        recordedTime = value.recordedTime,
        recordableTime = value.recordableTime,
        capturedPictures = value.capturedPictures,
        latestFileUrl = value.latestFileUrl,
        batteryState = ChargingState.get(value.batteryState),
        function = ShootingFunction.get(value.function),
        cameraError = value.cameraError?.mapNotNull { CameraError.get(it) },
        batteryInsert = value.batteryInsert,
        boardTemp = value.boardTemp,
        batteryTemp = value.batteryTemp,
    )
}
