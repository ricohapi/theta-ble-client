package com.ricoh360.thetableclient.transferred

import com.ricoh360.thetableclient.service.data.NumberAsIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * set access point request parameters
 */
@Serializable
internal data class SetAccessPointParams(
    /**
     * SSID (Up to 32 bytes)
     */
    val ssid: String,

    /**
     * (Optional) SSID stealth (true: enable, false: disable. Default
     * is false.)
     */
    val ssidStealth: Boolean? = null,

    /**
     * (Optional) Authentication mode("none", "WEP","WPA/WPA2 PSK")
     */
    val security: String? = null,

    /**
     * (Optional) Password.
     * This can be set when security is not "none"
     */
    val password: String? = null,

    /**
     * (Optional)
     * has a higher priority.
     */
    @Serializable(with = NumberAsIntSerializer::class)
    val connectionPriority: Int? = null,

    /**
     * (Optional) IP address allocation "dynamic" or
     * "static". Default is "dynamic"
     */
    val ipAddressAllocation: String? = null,

    /**
     * (Optional) IP address assigned to camera. This setting can be
     * set when ipAddressAllocation is "static"
     */
    val ipAddress: String? = null,

    /**
     * (Optional) Subnet mask. This setting can be set when
     * ipAddressAllocation is "static"
     */
    val subnetMask: String? = null,

    /**
     * (Optional) Default gateway. This setting can be set when
     * ipAddressAllocation is "static"
     */
    val defaultGateway: String? = null,

    /**
     * Proxy information to be used for the access point.
     * Also refer to _proxy option spec to set each parameter.
     */
    @SerialName("_proxy")
    val proxy: Proxy? = null,
)
