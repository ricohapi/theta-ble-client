package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.Serializable

@Serializable
internal data class DhcpLeaseAddress(
    val ipAddress: String,
    val macAddress: String,
    val hostName: String,
)
