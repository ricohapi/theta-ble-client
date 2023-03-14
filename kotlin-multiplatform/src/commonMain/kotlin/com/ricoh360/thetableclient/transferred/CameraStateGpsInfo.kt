package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.Serializable

@Serializable
internal data class CameraStateGpsInfo(
    val gpsInfo: CameraGpsInfo? = null,
)
