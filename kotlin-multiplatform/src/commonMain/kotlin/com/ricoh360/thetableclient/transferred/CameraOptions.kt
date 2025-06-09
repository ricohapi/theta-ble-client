package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CameraOptions(
    @SerialName("_accessInfo")
    val accessInfo: AccessInfo? = null,

    @SerialName("_cameraPower")
    val cameraPower: String? = null,

    @SerialName("captureMode")
    val captureMode: String? = null,

    @SerialName("_defaultWifiPassword")
    val defaultWifiPassword: String? = null,

    @SerialName("_networkType")
    val networkType: String? = null,

    @SerialName("_password")
    val password: String? = null,

    @SerialName("_ssid")
    val ssid: String? = null,

    @SerialName("_username")
    val username: String? = null,

    @SerialName("_wlanAntennaConfig")
    val wlanAntennaConfig: String? = null,

    @SerialName("_wlanFrequency")
    val wlanFrequency: Double? = null,
) {
    companion object {
        fun decode(jsonString: String): CameraOptions {
            val json = Json {
                ignoreUnknownKeys = true
            }
            return json.decodeFromString(jsonString)
        }
    }
}

@Serializable
internal data class GetOptionsParams(
    /**
     * option name list to be acquired
     */
    val optionNames: List<String>,
)

@Serializable
internal data class CameraOptionsParent(
    @SerialName("options")
    val options: CameraOptions,
) {
    companion object {
        fun decode(jsonString: String): CameraOptionsParent {
            val json = Json {
                ignoreUnknownKeys = true
            }
            return json.decodeFromString(jsonString)
        }
    }
}
