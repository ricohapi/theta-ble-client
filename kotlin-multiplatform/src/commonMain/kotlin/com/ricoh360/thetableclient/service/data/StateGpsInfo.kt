package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.transferred.CameraStateGpsInfo

/**
 * GPS information of ThetaState2
 */
data class StateGpsInfo(
    /**
     * GPS information
     */
    val gpsInfo: GpsInfo?,
) {
    internal constructor(value: CameraStateGpsInfo) : this(
        gpsInfo = value.gpsInfo?.let { GpsInfo(it) }
    )
}
