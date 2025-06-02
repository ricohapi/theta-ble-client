package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CameraInfo(
    val manufacturer: String,
    val model: String,
    val serialNumber: String,

    @SerialName("_wlanMacAddress")
    val wlanMacAddress: String? = null,

    @SerialName("_bluetoothMacAddress")
    val bluetoothMacAddress: String? = null,
    val firmwareVersion: String,
    val uptime: Int,
) {
    companion object {
        fun decode(jsonString: String): CameraInfo {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }
    }
}
