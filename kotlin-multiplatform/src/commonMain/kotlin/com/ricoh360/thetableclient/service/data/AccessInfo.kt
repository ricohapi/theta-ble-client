package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.service.data.values.WlanFrequency

data class AccessInfo(
    /**
     * SSID of the wireless LAN access point that THETA connects to
     */
    val ssid: String,

    /**
     * IP address of access point
     */
    val ipAddress: String,

    /**
     * subnet mask of access point
     */
    val subnetMask: String,

    /**
     * default gateway of access point
     */
    val defaultGateway: String,

    /**
     * proxy URL of access point
     */
    val proxyURL: String,

    /**
     * Radio frequency
     */
    val frequency: WlanFrequency,

    /**
     * WLAN signal strength
     * [dBm]
     */
    val wlanSignalStrength: Int,

    /**
     * WLAN signal level
     * 0~4
     */
    val wlanSignalLevel: Int,

    /**
     * LTE signal strength
     * [dBm]
     */
    val lteSignalStrength: Int,

    /**
     * LTE signal level
     * 0~4
     */
    val lteSignalLevel: Int,

    /**
     * client devices information
     */
    val dhcpLeaseAddress: List<DhcpLeaseAddress>?,
) {
    internal constructor(value: com.ricoh360.thetableclient.transferred.AccessInfo) : this(
        ssid = value.ssid,
        ipAddress = value.ipAddress,
        subnetMask = value.subnetMask,
        defaultGateway = value.defaultGateway,
        proxyURL = value.proxyURL,
        frequency = WlanFrequency.getFromValue(value.frequency),
        wlanSignalStrength = value.wlanSignalStrength,
        wlanSignalLevel = value.wlanSignalLevel,
        lteSignalStrength = value.lteSignalStrength,
        lteSignalLevel = value.lteSignalLevel,
        dhcpLeaseAddress = value.dhcpLeaseAddress?.let { list ->
            list.map { DhcpLeaseAddress(it) }
        }
    )

    internal fun toTransferred(): com.ricoh360.thetableclient.transferred.AccessInfo {
        return com.ricoh360.thetableclient.transferred.AccessInfo(
            ssid = ssid,
            ipAddress = ipAddress,
            subnetMask = subnetMask,
            defaultGateway = defaultGateway,
            proxyURL = proxyURL,
            frequency = frequency.stringValue ?: "",
            wlanSignalStrength = wlanSignalStrength,
            wlanSignalLevel = wlanSignalLevel,
            lteSignalStrength = lteSignalStrength,
            lteSignalLevel = lteSignalLevel,
            dhcpLeaseAddress = dhcpLeaseAddress?.let { list ->
                list.map { it.toTransferred() }
            }
        )
    }

    companion object {
        val keyName: String
            get() = "accessInfo"
    }
}
