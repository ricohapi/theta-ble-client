package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
internal data class CameraConnectedInfo(
    val ssid: String,
    val isConnected: Boolean,
    val isInternetAccessible: Boolean,
)

@Serializable
internal data class CameraConnectedWifiInfo(
    val wifiInfo: CameraConnectedInfo? = null,
    val ethernet: CameraConnectedInfo? = null,
    @SerialName("LTE")
    val lte: CameraConnectedInfo? = null,
) {
    companion object {
        fun decode(jsonString: String): CameraConnectedWifiInfo {
            val json = Json {
                ignoreUnknownKeys = true
            }
            return json.decodeFromString(jsonString)
        }
    }
}
