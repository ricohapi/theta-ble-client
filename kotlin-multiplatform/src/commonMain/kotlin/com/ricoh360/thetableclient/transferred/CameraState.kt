package com.ricoh360.thetableclient.transferred

import com.ricoh360.thetableclient.service.data.NumberAsIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CameraState(
    val batteryLevel: Float? = null,

    @SerialName("_captureStatus")
    val captureStatus: String? = null,

    @SerialName("_recordedTime")
    @Serializable(with = NumberAsIntSerializer::class)
    val recordedTime: Int? = null,

    @SerialName("_recordableTime")
    @Serializable(with = NumberAsIntSerializer::class)
    val recordableTime: Int? = null,

    @SerialName("_capturedPictures")
    @Serializable(with = NumberAsIntSerializer::class)
    val capturedPictures: Int? = null,

    @SerialName("_latestFileUrl")
    val latestFileUrl: String? = null,

    @SerialName("_batteryState")
    val batteryState: String? = null,

    @SerialName("_function")
    val function: String? = null,

    @SerialName("_cameraError")
    val cameraError: List<String>? = null,

    @SerialName("_batteryInsert")
    val batteryInsert: Boolean? = null,

    @SerialName("_boardTemp")
    @Serializable(with = NumberAsIntSerializer::class)
    val boardTemp: Int? = null,

    @SerialName("_batteryTemp")
    @Serializable(with = NumberAsIntSerializer::class)
    val batteryTemp: Int? = null,
) {
    companion object {
        fun decode(jsonString: String): CameraState {
            val json = Json {
                ignoreUnknownKeys = true
            }
            return json.decodeFromString(jsonString)
        }
    }
}
