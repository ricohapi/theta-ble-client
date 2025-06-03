package com.ricoh360.thetableclient.transferred

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class Peripheral(
    val device: String,
    val macAddress: String,
    val pairing: Boolean,
    val status: String,
) {
    companion object {
        fun decode(jsonString: String): Peripheral {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(jsonString)
        }
    }
}
