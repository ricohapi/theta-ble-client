package com.ricoh360.thetableclient.service.data.ble

import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_UNKNOWN_VALUE
import com.ricoh360.thetableclient.ThetaBle

/**
 * Plugin orders
 *
 * When not specifying, set 0. If an 0 is placed mid-way, it will be moved to the front.
 * Specifying zero plugin will result in an error.
 *
 * RICOH THETA Z1 or later.
 */
data class PluginOrders(
    /**
     * Plugin number to be set the first plugin
     */
    val first: Int,

    /**
     * Plugin number to be set the second plugin
     */
    val second: Int,

    /**
     * Plugin number to be set the third plugin
     */
    val third: Int,
) {
    companion object {
        val keyName: String
            get() = "pluginOrders"

        @Throws(Throwable::class)
        fun newInstance(bleData: ByteArray): PluginOrders {
            if (bleData.isEmpty()) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            } else if (bleData.size < 3) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_UNKNOWN_VALUE)
            }
            return PluginOrders(
                first = bleData[0].toInt(),
                second = bleData[1].toInt(),
                third = bleData[2].toInt(),
            )
        }
    }

    internal fun toBleData(): ByteArray {
        val result = ByteArray(3)
        result[0] = first.toByte()
        result[1] = second.toByte()
        result[2] = third.toByte()
        return result
    }
}
