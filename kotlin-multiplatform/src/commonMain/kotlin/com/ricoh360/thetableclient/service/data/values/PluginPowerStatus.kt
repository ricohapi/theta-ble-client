package com.ricoh360.thetableclient.service.data.values


/**
 * Plugin power status (Kind of action)
 * @see PluginControl
 */
enum class PluginPowerStatus(internal val ble: Byte?) {
    /**
     * Running (Start plugin)
     */
    RUNNING(0),

    /**
     * Stop
     */
    STOP(1),
    ;

    companion object {
        val keyName: String
            get() = "pluginControl"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return PluginPowerStatus
         */
        internal fun getFromBle(bleData: Byte): PluginPowerStatus? {
            return entries.firstOrNull { it.ble == bleData }
        }
    }
}
