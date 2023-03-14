package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.transferred.CameraState2

/**
 * Mutable values representing THETA state
 */
data class ThetaState2(
    /**
     * gpsInfo set by API.
     */
    val externalGpsInfo: StateGpsInfo?,

    /**
     * gpsInfo from the built-in GPS module.
     */
    val internalGpsInfo: StateGpsInfo?,
) {
    internal constructor(value: CameraState2) : this(
        externalGpsInfo = value.externalGpsInfo?.let { StateGpsInfo(value.externalGpsInfo) },
        internalGpsInfo = value.internalGpsInfo?.let { StateGpsInfo(value.internalGpsInfo) },
    )
}
