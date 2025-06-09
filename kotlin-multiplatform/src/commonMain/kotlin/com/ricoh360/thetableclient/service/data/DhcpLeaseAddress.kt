package com.ricoh360.thetableclient.service.data

/**
 * client devices information
 */
data class DhcpLeaseAddress(
    /**
     * IP address of client device
     */
    val ipAddress: String,

    /**
     * MAC address of client device
     */
    val macAddress: String,

    /**
     * host name of client device
     */
    val hostName: String,
) {
    internal constructor(value: com.ricoh360.thetableclient.transferred.DhcpLeaseAddress) : this(
        ipAddress = value.ipAddress,
        macAddress = value.macAddress,
        hostName = value.hostName,
    )

    internal fun toTransferred(): com.ricoh360.thetableclient.transferred.DhcpLeaseAddress {
        return com.ricoh360.thetableclient.transferred.DhcpLeaseAddress(
            ipAddress = ipAddress,
            macAddress = macAddress,
            hostName = hostName,
        )
    }

    companion object {
        val keyName: String
            get() = "dhcpLeaseAddress"
    }
}
