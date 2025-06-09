package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccessInfo(
    val ssid: String,
    val ipAddress: String,
    val subnetMask: String,
    val defaultGateway: String,
    val proxyURL: String,
    val frequency: String,
    val wlanSignalStrength: Int,
    val wlanSignalLevel: Int,
    val lteSignalStrength: Int,
    val lteSignalLevel: Int,
    @SerialName("_dhcpLeaseAddress")
    var dhcpLeaseAddress: List<DhcpLeaseAddress>? = null,
)
