package com.ricoh360.thetableclient.service.data.ble

import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_UNKNOWN_VALUE
import com.ricoh360.thetableclient.ThetaBle

/**
 * Plugin list
 */
data class PluginList(
    /**
     * Plugin number list
     */
    val plugins: List<Int>,
) {
    companion object {
        val keyName: String
            get() = "pluginList"

        @Throws(Throwable::class)
        fun newInstance(bleData: ByteArray): PluginList {
            if (bleData.isEmpty()) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            } else if (bleData.size < bleData[0] + 1) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_UNKNOWN_VALUE)
            }
            return PluginList(List(bleData[0].toInt()) { bleData[it + 1].toInt() })
        }
    }

    internal fun toBleData(): ByteArray {
        val result = ByteArray(plugins.size + 1)
        result[0] = plugins.size.toByte()
        for ((index, _) in plugins.withIndex()) {
            result[index + 1] = plugins[index].toByte()
        }
        return result
    }
}
