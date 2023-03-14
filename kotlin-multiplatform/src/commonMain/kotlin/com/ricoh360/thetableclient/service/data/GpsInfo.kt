package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.transferred.CameraGpsInfo

/**
 * GPS information
 */
data class GpsInfo(
    /**
     * Latitude (-90.000000 – 90.000000)
     */
    val lat: Float? = null,

    /**
     * Longitude (-180.000000 – 180.000000)
     */
    val lng: Float? = null,

    /**
     * Altitude (meters)
     */
    val altitude: Float? = null,

    /**
     * Location information acquisition time
     * YYYY:MM:DD hh:mm:ss+(-)hh:mm
     * hh is in 24-hour time, +(-)hh:mm is the time zone
     */
    val dateTimeZone: String? = null,

    /**
     * Geodetic reference
     * When GPS is enabled: WGS84
     */
    val datum: String? = null,
) {
    internal constructor(value: CameraGpsInfo) : this(
        lat = value.lat,
        lng = value.lng,
        altitude = value.altitude,
        dateTimeZone = value.dateTimeZone,
        datum = value.datum
    )

    companion object {
        val keyName: String
            get() = "gpsInfo"

    }
}
