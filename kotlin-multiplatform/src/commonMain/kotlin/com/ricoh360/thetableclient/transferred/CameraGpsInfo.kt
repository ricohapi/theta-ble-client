package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CameraGpsInfo(
    val lat: Float? = null,

    val lng: Float? = null,

    @SerialName("_altitude")
    val altitude: Float? = null,

    @SerialName("_dateTimeZone")
    val dateTimeZone: String? = null,

    @SerialName("_datum")
    val datum: String? = null,
)
