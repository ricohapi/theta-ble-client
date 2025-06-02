package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.transferred.CameraConnectedInfo
import com.ricoh360.thetableclient.transferred.CameraConnectedWifiInfo


/**
 * Wi-Fi connection status (Wifi connection, SSID, Internet access)
 */
data class ConnectedInfo(
    /**
     * SSID
     *
     * If there is no SSID to connect to, "unknown ssid"
     */
    val ssid: String,

    /**
     * Whether you are connected to an Access Point
     */
    val isConnected: Boolean,

    /**
     * Availability of Internet access
     *
     * Whether ping 8.8.8.8 is accessible
     */
    val isInternetAccessible: Boolean,
) {
    internal constructor(value: CameraConnectedInfo) : this(
        ssid = value.ssid,
        isConnected = value.isConnected,
        isInternetAccessible = value.isInternetAccessible,
    )

    companion object {
        val keyName: String
            get() = "connectedInfo"

    }
}

/**
 * Connection Status by Type (Wi-Fi, Ethernet, LTE)
 */
data class ConnectedWifiInfo(
    /**
     * State of Wi-Fi connection case
     */
    val wifiInfo: ConnectedInfo? = null,

    /**
     * State of ethernet connection case
     */
    val ethernet: ConnectedInfo? = null,

    /**
     * State of LTE connection case
     */
    val lte: ConnectedInfo? = null,
) {
    internal constructor(value: CameraConnectedWifiInfo) : this(
        wifiInfo = value.wifiInfo?.let { ConnectedInfo(it) },
        ethernet = value.ethernet?.let { ConnectedInfo(it) },
        lte = value.lte?.let { ConnectedInfo(it) },
    )

    companion object {
        val keyName: String
            get() = "connectedWifiInfo"
    }
}

