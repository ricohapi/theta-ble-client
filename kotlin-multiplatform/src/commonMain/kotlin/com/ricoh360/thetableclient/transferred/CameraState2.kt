package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CameraState2(
    @SerialName("_externalGpsInfo")
    val externalGpsInfo: CameraStateGpsInfo? = null,

    @SerialName("_internalGpsInfo")
    val internalGpsInfo: CameraStateGpsInfo? = null,
) {
    companion object {
        fun decode(jsonString: String): CameraState2 {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }
    }
}
