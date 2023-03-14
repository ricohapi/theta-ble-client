package com.ricoh360.thetableclient.service.data.ble

import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_UNKNOWN_VALUE
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.service.data.values.PluginPowerStatus

/**
 * Plugin Control
 */
data class PluginControl(
    /**
     * Plugin power status (Kind of action)
     * (0: Running (Start plugin)、1: Stop)
     */
    val pluginControl: PluginPowerStatus,

    /**
     * Target plugin number. Set the target plugin number to Plugin Orders before write.
     * This parameter is ignored when Plugin Control parameter is 1 (stop).
     * RICOH THETA Z1 or later
     */
    val plugin: Int?,
) {
    companion object {
        val keyName: String
            get() = "pluginControl"

        @Throws(Throwable::class)
        fun newInstance(bleData: ByteArray): PluginControl {
            if (bleData.isEmpty()) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            val pluginControl = PluginPowerStatus.getFromBle(bleData[0])
                ?: throw ThetaBle.ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${bleData[0]}")
            val plugin = if (bleData.size == 2) bleData[1].toInt() else null
            return PluginControl(pluginControl, plugin)
        }
    }

    internal fun toBleData(): ByteArray {
        pluginControl.ble?:throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_UNKNOWN_VALUE)
        return when (plugin) {
            null -> {
                val result = ByteArray(1)
                result[0] = pluginControl.ble
                result
            }

            else -> {
                // RICOH THETA Z1 or later
                val result = ByteArray(2)
                result[0] = pluginControl.ble
                result[1] = plugin.toByte()
                result
            }
        }
    }
}
