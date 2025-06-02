package com.ricoh360.thetableclient.transferred

import com.ricoh360.thetableclient.service.data.values.NetworkType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class WlanNetworkType(
    val type: String
) {
    companion object {
        fun decode(jsonString: String): NetworkType {
            val json = Json {  ignoreUnknownKeys = true}
            val data = json.decodeFromString(jsonString) as WlanNetworkType
            return NetworkType.getFromValue(data.type)
        }
    }
}
